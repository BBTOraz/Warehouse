package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.service.InventoryTransactionService;
import bbt.tao.warehouse.service.impl.InventoryTransactionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    public InventoryTransactionController(InventoryTransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<InventoryTransactionDTO>> getAllTransactions() {
        List<InventoryTransactionDTO> transactions = transactionService.findAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> getTransactionById(@PathVariable Long id) {
        return transactionService.findTransactionById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction not found with id: " + id));
    }

    @PostMapping("/receiving")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> createReceivingTransaction(
            @RequestBody InventoryTransactionDTO transaction) {
        try {
            if (transaction == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Transaction data cannot be empty");
            }

            InventoryTransactionDTO savedTransaction = transactionService.createReceivingTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/shipping")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> createShippingTransaction(
            @RequestBody InventoryTransactionDTO transaction) {
        try {
            if (transaction == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Transaction data cannot be empty");
            }

            InventoryTransactionDTO savedTransaction = transactionService.createShippingTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (InsufficientStockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> createTransferTransaction(
            @RequestBody InventoryTransactionDTO transaction) {
        try {
            if (transaction == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Transaction data cannot be empty");
            }

            InventoryTransactionDTO savedTransaction = transactionService.createTransferTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (InsufficientStockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/adjustment")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> createAdjustmentTransaction(
            @RequestBody InventoryTransactionDTO transaction) {
        try {
            if (transaction == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Transaction data cannot be empty");
            }

            InventoryTransactionDTO savedTransaction = transactionService.createAdjustmentTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (InsufficientStockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            // Check if transaction exists
            transactionService.findTransactionById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Transaction not found with id: " + id));

            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}