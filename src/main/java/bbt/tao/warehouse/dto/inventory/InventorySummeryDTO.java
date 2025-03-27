package bbt.tao.warehouse.dto.inventory;

import bbt.tao.warehouse.model.enums.InventoryStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventorySummeryDTO {
    private Long id;
    private String inventoryNumber;
    private InventoryStatus status;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endDate;
}
