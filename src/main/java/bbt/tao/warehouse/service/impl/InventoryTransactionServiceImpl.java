package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.inventory.InventoryItemDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.mapper.InventoryItemMapper;
import bbt.tao.warehouse.mapper.InventoryTransactionMapper;
import bbt.tao.warehouse.model.*;
import bbt.tao.warehouse.model.enums.TransactionType;
import bbt.tao.warehouse.repository.*;
import bbt.tao.warehouse.service.InventoryTransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionMapper transactionMapper;
    private final InventoryItemMapper inventoryItemMapper;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<InventoryTransactionDTO> findAllTransactions() {
        List<InventoryTransaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InventoryTransactionDTO> findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO);
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByProduct(Long productId) {
        List<InventoryTransaction> transactions = transactionRepository.findByProduct_Id(productId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByType(TransactionType transactionType) {
        List<InventoryTransaction> transactions = transactionRepository.findByTransactionType(transactionType);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsBySupplier(Long supplierId) {
        List<InventoryTransaction> transactions = transactionRepository.findBySupplier_Id(supplierId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByCustomer(Long customerId) {
        List<InventoryTransaction> transactions = transactionRepository.findByCustomer_Id(customerId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByDocumentNumber(String documentNumber) {
        List<InventoryTransaction> transactions = transactionRepository.findByDocumentNumber(documentNumber);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryTransaction> transactions = transactionRepository.findByDateRange(startDate, endDate);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByLocation(Long locationId) {
        List<InventoryTransaction> transactions = transactionRepository.findByLocation(locationId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryTransactionDTO createReceivingTransaction(InventoryTransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }


        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.RECEIVING);
        transaction.setTransactionDate(LocalDateTime.now());


        InventoryTransaction savedTransaction = transactionRepository.save(checkIfEntityExists(transaction));

        // Update inventory
        updateInventoryOnReceiving(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    private InventoryTransaction checkIfEntityExists(InventoryTransaction transaction) {
        if (transaction.getSupplier() != null && transaction.getSupplier().getId() != null) {
            Supplier existingSupplier = supplierRepository.findById(transaction.getSupplier().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: "
                            + transaction.getSupplier().getId()));
            transaction.setSupplier(existingSupplier);
        } else {
            transaction.setSupplier(null);
        }

        // Аналогично для customer, product, location и т.д.
        if (transaction.getCustomer() != null && transaction.getCustomer().getId() != null) {
            Customer existingCustomer = customerRepository.findById(transaction.getCustomer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: "
                            + transaction.getCustomer().getId()));
            transaction.setCustomer(existingCustomer);
        } else {
            transaction.setCustomer(null);
        }

        if (transaction.getProduct() != null && transaction.getProduct().getId() != null) {
            Product existingProduct = productRepository.findById(transaction.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: "
                            + transaction.getProduct().getId()));
            transaction.setProduct(existingProduct);
        }  else
            transaction.setProduct(null);

        if(transaction.getSourceLocation() != null && transaction.getSourceLocation().getId() != null) {
            Location existingSourceLocation = locationRepository.findById(transaction.getSourceLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Source location not found with id: "
                            + transaction.getSourceLocation().getId()));
            transaction.setSourceLocation(existingSourceLocation);
        } else
            transaction.setSourceLocation(null);

        if(transaction.getDestinationLocation() != null && transaction.getDestinationLocation().getId() != null) {
            Location existingDestinationLocation = locationRepository.findById(transaction.getDestinationLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Destination location not found with id: "
                            + transaction.getDestinationLocation().getId()));
            transaction.setDestinationLocation(existingDestinationLocation);
        } else
            transaction.setDestinationLocation(null);

        return transaction;
    }

    @Override
    public InventoryTransactionDTO createShippingTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.SHIPPING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Check sufficient stock
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(checkIfEntityExists(transaction));

        // Update inventory
        updateInventoryOnShipping(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public InventoryTransactionDTO createTransferTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionDate(LocalDateTime.now());
        // Check sufficient stock
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(checkIfEntityExists(transaction));

        // Update inventory
        updateInventoryOnTransfer(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public InventoryTransactionDTO createAdjustmentTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.ADJUSTMENT);
        transaction.setTransactionDate(LocalDateTime.now());

        if (transaction.getQuantity() < 0) {
            checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                    transaction.getBatchNumber(), Math.abs(transaction.getQuantity()));
        }

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(checkIfEntityExists(transaction));

        // Update inventory
        updateInventoryOnAdjustment(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<InventoryItemDTO> getStockLevels(Long productId) {
        List<InventoryItem> items = inventoryItemRepository.findByProduct_Id(productId);
        return items.stream()
                .map(inventoryItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByUser(Long userId) {
        List<InventoryTransaction> transactions = transactionRepository.findByUser_Id(userId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryTransactionDTO updateTransaction(Long id, InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        InventoryTransaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));

        InventoryTransaction updatedData = transactionMapper.toEntity(transactionDTO);


        existingTransaction.setTransactionType(updatedData.getTransactionType());
        existingTransaction.setQuantity(updatedData.getQuantity());
        existingTransaction.setBatchNumber(updatedData.getBatchNumber());
        existingTransaction.setExpirationDate(updatedData.getExpirationDate());
        existingTransaction.setDocumentNumber(updatedData.getDocumentNumber());
        existingTransaction.setNotes(updatedData.getNotes());

        if (updatedData.getProduct() != null && updatedData.getProduct().getId() != null) {
            Product existingProduct = productRepository.findById(updatedData.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + updatedData.getProduct().getId()));
            existingTransaction.setProduct(existingProduct);
        } else {
            existingTransaction.setProduct(null);
        }

        if (updatedData.getSupplier() != null && updatedData.getSupplier().getId() != null) {
            Supplier existingSupplier = supplierRepository.findById(updatedData.getSupplier().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + updatedData.getSupplier().getId()));
            existingTransaction.setSupplier(existingSupplier);
        } else {
            existingTransaction.setSupplier(null);
        }

        if (updatedData.getCustomer() != null && updatedData.getCustomer().getId() != null) {
            Customer existingCustomer = customerRepository.findById(updatedData.getCustomer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + updatedData.getCustomer().getId()));
            existingTransaction.setCustomer(existingCustomer);
        } else {
            existingTransaction.setCustomer(null);
        }

        if (updatedData.getSourceLocation() != null && updatedData.getSourceLocation().getId() != null) {
            Location existingSourceLocation = locationRepository.findById(updatedData.getSourceLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Source location not found with id: " + updatedData.getSourceLocation().getId()));
            existingTransaction.setSourceLocation(existingSourceLocation);
        } else {
            existingTransaction.setSourceLocation(null);
        }

        if (updatedData.getDestinationLocation() != null && updatedData.getDestinationLocation().getId() != null) {
            Location existingDestinationLocation = locationRepository.findById(updatedData.getDestinationLocation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Destination location not found with id: " + updatedData.getDestinationLocation().getId()));
            existingTransaction.setDestinationLocation(existingDestinationLocation);
        } else {
            existingTransaction.setDestinationLocation(null);
        }

        InventoryTransaction savedTransaction = transactionRepository.save(existingTransaction);


        return transactionMapper.toDTO(savedTransaction);
    }



    private void updateInventoryOnTransfer(InventoryTransaction transaction) {
        Optional<InventoryItem> sourceItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        if (sourceItemOptional.isPresent()) {
            InventoryItem sourceItem = sourceItemOptional.get();
            double newQuantity = sourceItem.getQuantity() - transaction.getQuantity();

            if (newQuantity > 0) {
                sourceItem.setQuantity(newQuantity);
                sourceItem.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(sourceItem);
            } else {
                inventoryItemRepository.delete(sourceItem);
            }
        }

        Optional<InventoryItem> destItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getDestinationLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem destItem;

        if (destItemOptional.isPresent()) {
            destItem = destItemOptional.get();
            destItem.setQuantity(destItem.getQuantity() + transaction.getQuantity());
        } else {
            destItem = new InventoryItem();
            destItem.setProduct(transaction.getProduct());
            destItem.setLocation(transaction.getDestinationLocation());
            destItem.setQuantity(transaction.getQuantity());
            destItem.setBatchNumber(transaction.getBatchNumber());
            destItem.setExpirationDate(transaction.getExpirationDate());
        }

        destItem.setLastInventoryDate(LocalDateTime.now());
        inventoryItemRepository.save(destItem);
    }

    private void updateInventoryOnAdjustment(InventoryTransaction transaction) {
        Optional<InventoryItem> itemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem item;

        if (itemOptional.isPresent()) {
            // Update existing inventory item
            item = itemOptional.get();
            double newQuantity = item.getQuantity() + transaction.getQuantity();

            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
                item.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(item);
            } else {
                // Remove inventory record if quantity becomes zero or negative
                inventoryItemRepository.delete(item);
            }
        } else if (transaction.getQuantity() > 0) {
            // Create new inventory record for positive adjustment when no record exists
            item = new InventoryItem();
            item.setProduct(transaction.getProduct());
            item.setLocation(transaction.getSourceLocation());
            item.setQuantity(transaction.getQuantity());
            item.setBatchNumber(transaction.getBatchNumber());
            item.setExpirationDate(transaction.getExpirationDate());
            item.setLastInventoryDate(LocalDateTime.now());
            inventoryItemRepository.save(item);
        }
    }

    private void checkSufficientStock(Long productId, Long locationId, String batchNumber, Double quantity) throws InsufficientStockException {
        System.out.println("Checking sufficient stock for product ID: " + productId + ", location ID: " + locationId + ", batch number: " + batchNumber + ", quantity: " + quantity);
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(productId, locationId, batchNumber);
        if (inventoryItemOptional.isPresent()) {
            log.info("{} {}", inventoryItemOptional, quantity);
            if (inventoryItemOptional.get().getQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product ID: " + productId + " at location ID: " + locationId);
            }
        }else throw new ObjectNotFoundException("Inventory item not found ", inventoryItemOptional);
    }

    private void updateInventoryOnReceiving(InventoryTransaction transaction) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getDestinationLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem inventoryItem;

        if (inventoryItemOptional.isPresent()) {
            // Update existing inventory
            inventoryItem = inventoryItemOptional.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() + transaction.getQuantity());
        } else {
            // Create new inventory record
            inventoryItem = new InventoryItem();
            inventoryItem.setProduct(transaction.getProduct());
            inventoryItem.setLocation(transaction.getDestinationLocation());
            inventoryItem.setQuantity(transaction.getQuantity());
            inventoryItem.setBatchNumber(transaction.getBatchNumber());
            inventoryItem.setExpirationDate(transaction.getExpirationDate());
        }

        inventoryItem.setLastInventoryDate(LocalDateTime.now());
        inventoryItemRepository.save(inventoryItem);
    }

    private void updateInventoryOnShipping(InventoryTransaction transaction) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        if (inventoryItemOptional.isPresent()) {
            InventoryItem inventoryItem = inventoryItemOptional.get();
            double newQuantity = inventoryItem.getQuantity() - transaction.getQuantity();

            if (newQuantity > 0) {
                inventoryItem.setQuantity(newQuantity);
                inventoryItem.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(inventoryItem);
            } else {
                // Если остаток равен 0, удаляем запись
                inventoryItemRepository.delete(inventoryItem);
            }
        }
    }
}
