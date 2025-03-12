package bbt.tao.warehouse.dto.inventory;


import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import lombok.*;
import java.time.LocalDateTime;
import bbt.tao.warehouse.model.enums.InventoryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {
    private Long id;
    private String inventoryNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InventoryStatus status;
    private WarehouseDTO warehouse;
    private UserDTO createdBy;
}



