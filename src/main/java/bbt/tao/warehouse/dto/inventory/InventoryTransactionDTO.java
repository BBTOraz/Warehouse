package bbt.tao.warehouse.dto.inventory;

import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.location.LocationSummaryDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.dto.product.ProductSummaryDTO;
import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import lombok.*;
import java.time.LocalDateTime;
import bbt.tao.warehouse.model.enums.TransactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionDTO {
    private Long id;
    private TransactionType transactionType;
    private ProductSummaryDTO product;
    private LocationDTO sourceLocation;
    private LocationDTO destinationLocation;
    private Double quantity;
    private String batchNumber;
    private LocalDateTime expirationDate;
    private String documentNumber;
    private SupplierDTO supplier;
    private CustomerDTO customer;
    private UserDTO user;
    private LocalDateTime transactionDate;
    private String notes;
}

