package bbt.tao.warehouse.controller.manager;

import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.mapper.UserMapper;
import bbt.tao.warehouse.model.enums.ActionType;
import bbt.tao.warehouse.model.enums.TransactionType;
import bbt.tao.warehouse.security.CustomUserDetails;
import bbt.tao.warehouse.service.*;
import bbt.tao.warehouse.service.impl.InventoryTransactionServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
@RequestMapping("/transactions")
public class ManagerTransactionsController {

    private final InventoryTransactionServiceImpl transactionService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final WarehouseService warehouseService;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;


    @GetMapping
    public String getAllTransactions(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime endDate,
            @RequestParam(required = false) Long locationId,
            Model model) {

        List<InventoryTransactionDTO> transactions;

        if (productId != null) {
            transactions = transactionService.findTransactionsByProduct(productId);
        } else if (transactionType != null) {
            transactions = transactionService.findTransactionsByType(transactionType);
        } else if (supplierId != null) {
            transactions = transactionService.findTransactionsBySupplier(supplierId);
        } else if (customerId != null) {
            transactions = transactionService.findTransactionsByCustomer(customerId);
        } else if (documentNumber != null && !documentNumber.isEmpty()) {
            transactions = transactionService.findTransactionsByDocumentNumber(documentNumber);
        } else if (startDate != null && endDate != null) {
            transactions = transactionService.findTransactionsByDateRange(startDate, endDate);
        } else if (locationId != null) {
            transactions = transactionService.findTransactionsByLocation(locationId);
        } else {
            transactions = transactionService.findAllTransactions();
        }

        addCommonModelAttributes(model);
        model.addAttribute("transactions", transactions);
        model.addAttribute("searchParams", new SearchParams(productId, transactionType,
                supplierId, customerId, documentNumber, startDate, endDate, locationId));

        return "manager/transactions/list";
    }

    @GetMapping("/{id}")
    public String getTransactionDetails(@PathVariable Long id, Model model) {
        Optional<InventoryTransactionDTO> transaction = transactionService.findTransactionById(id);

        if (transaction.isPresent()) {
            model.addAttribute("transaction", transaction.get());
            return "manager/transactions/details";
        }

        return "redirect:/transactions";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("transactionDTO", new InventoryTransactionDTO());
        addCommonModelAttributes(model);
        return "manager/transactions/transaction-form";
    }

    @PostMapping("/create")
    public String createTransaction(
            @ModelAttribute("transactionDTO") InventoryTransactionDTO transactionDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCommonModelAttributes(model);
            return "manager/transactions/transaction-form";
        }

        try {
            UserDTO userDTO =
                    ((CustomUserDetails) authentication.getPrincipal()).getUser();
            transactionDTO.setUser(userDTO);

            if (transactionDTO.getTransactionDate() == null) {
                transactionDTO.setTransactionDate(LocalDateTime.now());
            }
            log.info("Transaction Date: {}", transactionDTO);
            InventoryTransactionDTO createdTransaction = switch (transactionDTO.getTransactionType()) {
                case RECEIVING -> transactionService.createReceivingTransaction(transactionDTO);
                case SHIPPING -> transactionService.createShippingTransaction(transactionDTO);
                case TRANSFER -> transactionService.createTransferTransaction(transactionDTO);
                case ADJUSTMENT -> transactionService.createAdjustmentTransaction(transactionDTO);
                default -> throw new IllegalArgumentException("Неизвестный тип транзакции");
            };

            String message = "Пользователь " + userDTO.getUsername() + " создал транзакцию " + createdTransaction.getDocumentNumber();
            auditLogService.logAction(userMapper.toEntity(userDTO), ActionType.CREATE, "Transaction", createdTransaction.getId(), message, "192.168.1.101");
            redirectAttributes.addFlashAttribute("successMessage", "Транзакция успешно создана");
            return "redirect:/transactions/" + createdTransaction.getId();

        } catch (InsufficientStockException e) {
            bindingResult.rejectValue("quantity", "error.insufficientStock", "Недостаточно товара на складе");
            addCommonModelAttributes(model);
            return "manager/transactions/transaction-form";
        } catch (Exception e) {
            bindingResult.reject("error.transaction", "Ошибка при создании транзакции: " + e.getMessage());
            addCommonModelAttributes(model);
            return "manager/transactions/transaction-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<InventoryTransactionDTO> transaction = transactionService.findTransactionById(id);

        if (transaction.isPresent()) {
            model.addAttribute("transactionDTO", transaction.get());
            addCommonModelAttributes(model);
            return "manager/transactions/edit";
        }

        return "redirect:/transactions";
    }

    @PostMapping("/{id}/update")
    public String updateTransaction(
            @PathVariable Long id,
            @ModelAttribute("transactionDTO") InventoryTransactionDTO transactionDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addCommonModelAttributes(model);
            return "manager/transactions/edit";
        }

        try {
            UserDTO currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
            transactionDTO.setUser(currentUser);

            InventoryTransactionDTO updatedTransaction = transactionService.updateTransaction(id, transactionDTO);

            String message = "Пользователь " + currentUser.getUsername() + " изменил транзакцию " + updatedTransaction.getDocumentNumber();
            auditLogService.logAction(userMapper.toEntity(currentUser), ActionType.ADJUST, "Транзакция",  transactionDTO.getId(), message, "192.168.1.101");

            redirectAttributes.addFlashAttribute("successMessage", "Транзакция успешно обновлена");
            return "redirect:/transactions/" + updatedTransaction.getId();
        } catch (InsufficientStockException e) {
            bindingResult.rejectValue("quantity", "error.insufficientStock", "Недостаточно товара на складе");
            addCommonModelAttributes(model);
            return "manager/transactions/edit";
        } catch (Exception e) {
            bindingResult.reject("error.transaction", "Ошибка при обновлении транзакции: " + e.getMessage());
            addCommonModelAttributes(model);
            return "manager/transactions/edit";
        }
    }


    @PostMapping("/{id}/changeType")
    public String changeTransactionType(
            @PathVariable Long id,
            @RequestParam TransactionType newType,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<InventoryTransactionDTO> transactionOpt = transactionService.findTransactionById(id);

            if (transactionOpt.isPresent()) {
                InventoryTransactionDTO transactionDTO = transactionOpt.get();
                transactionDTO.setTransactionType(newType);

                transactionService.deleteTransaction(id);

                UserDTO userDTO = ((CustomUserDetails) authentication.getPrincipal()).getUser();
                transactionDTO.setUser(userDTO);

                InventoryTransactionDTO updatedTransaction = switch (newType) {
                    case RECEIVING -> transactionService.createReceivingTransaction(transactionDTO);
                    case SHIPPING -> transactionService.createShippingTransaction(transactionDTO);
                    case TRANSFER -> transactionService.createTransferTransaction(transactionDTO);
                    case ADJUSTMENT -> transactionService.createAdjustmentTransaction(transactionDTO);
                    default -> throw new IllegalArgumentException("Неизвестный тип транзакции");
                };

                String message = "Пользователь " + userDTO.getUsername() + " изменил тип транзакции на " + newType;
                auditLogService.logAction(userMapper.toEntity(userDTO), ActionType.ADJUST, "Транзакция",  transactionDTO.getId(), message, "192.168.1.101");

                redirectAttributes.addFlashAttribute("successMessage", "Тип транзакции успешно изменен");
                return "redirect:/transactions/" + updatedTransaction.getId();
            }

            redirectAttributes.addFlashAttribute("errorMessage", "Транзакция не найдена");

        } catch (InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Недостаточно товара на складе");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при изменении типа транзакции: " + e.getMessage());
        }

        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String deleteTransaction(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Транзакция успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении транзакции: " + e.getMessage());
        }
        UserDTO userDTO = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        String message = "Пользователь " + userDTO.getUsername() + " удалил транзакцию " + id;
        auditLogService.logAction(userMapper.toEntity(userDTO), ActionType.DELETE, "Транзакция",  id, message, "192.168.1.101");
        return "redirect:/transactions";
    }

    // Вспомогательный метод для добавления общих атрибутов в модель
    private void addCommonModelAttributes(Model model) {
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("products", productService.findAllProducts());
        model.addAttribute("locations", warehouseService.findAllLocations());
        model.addAttribute("suppliers", supplierService.findAllSuppliers());
        model.addAttribute("customers", customerService.findAllCustomers());
        model.addAttribute("warehouses", warehouseService.findAllWarehouses());
    }
        private record SearchParams(Long productId, TransactionType transactionType, Long supplierId, Long customerId,
                                    String documentNumber, LocalDateTime startDate, LocalDateTime endDate,
                                    Long locationId) {

    }
}