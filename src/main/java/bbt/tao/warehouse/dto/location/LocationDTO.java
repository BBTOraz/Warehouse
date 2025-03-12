package bbt.tao.warehouse.dto.location;


import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private Long id;
    private String code;
    private String description;
    private WarehouseDTO warehouse;
    private Double maxWeight;
    private Double maxVolume;
}



