package bbt.tao.warehouse.dto.inventory;

import bbt.tao.warehouse.model.enums.InventoryStatus;
import lombok.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventorySummeryDTO {
    private Long id;
    private String inventoryNumber;
    private InventoryStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
