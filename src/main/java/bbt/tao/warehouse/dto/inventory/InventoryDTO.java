package bbt.tao.warehouse.dto.inventory;


import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import lombok.*;
import java.time.LocalDateTime;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {
    private Long id;
    private String inventoryNumber;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endDate;
    private InventoryStatus status;
    private WarehouseDTO warehouse;
    private UserDTO createdBy;
}



