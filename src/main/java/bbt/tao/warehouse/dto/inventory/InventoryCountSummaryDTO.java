package bbt.tao.warehouse.dto.inventory;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCountSummaryDTO {
    private Long id;
    private Long inventoryId;
    private Long productId;
    private Long locationId;
    private Double expectedQuantity;
    private Double actualQuantity;
    private String batchNumber;
    private LocalDateTime countDate;
}
