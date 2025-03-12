package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.model.InventoryTransaction;
import bbt.tao.warehouse.model.enums.TransactionType;
import bbt.tao.warehouse.service.InventoryTransactionService;
import bbt.tao.warehouse.service.impl.InventoryTransactionServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    public InventoryTransactionController(InventoryTransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InventoryTransaction> getAllTransactions() {
        return transactionService.findAllTransactions();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Optional<InventoryTransaction> getTransactionById(@PathVariable Long id) {
        return transactionService.findTransactionById(id);
    }

    @PostMapping("/receiving")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventoryTransaction createReceivingTransaction(@RequestBody InventoryTransaction transaction) {
        return transactionService.createReceivingTransaction(transaction);
    }

    @PostMapping("/shipping")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventoryTransaction createShippingTransaction(@RequestBody InventoryTransaction transaction) throws InsufficientStockException {
        return transactionService.createShippingTransaction(transaction);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventoryTransaction createTransferTransaction(@RequestBody InventoryTransaction transaction) throws InsufficientStockException {
        return transactionService.createTransferTransaction(transaction);
    }

    @PostMapping("/adjustment")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventoryTransaction createAdjustmentTransaction(@RequestBody InventoryTransaction transaction) throws InsufficientStockException {
        return transactionService.createAdjustmentTransaction(transaction);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
