package bbt.tao.warehouse.dto.inventory;

import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.dto.product.ProductSummaryDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCountDTO {
    private Long id;
    private InventoryDTO inventory;
    private ProductSummaryDTO product;
    private LocationDTO location;
    private Double expectedQuantity;
    private Double actualQuantity;
    private String batchNumber;
    private LocalDateTime countDate;
    private UserDTO countedBy;
    private String notes;
}
