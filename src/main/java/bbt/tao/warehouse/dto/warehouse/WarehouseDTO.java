package bbt.tao.warehouse.dto.warehouse;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Boolean isActive;
}




