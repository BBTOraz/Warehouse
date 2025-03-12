package bbt.tao.warehouse.dto.location;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSummaryDTO {
    private Long id;
    private String code;
    private String description;
    private Long warehouseId;
    private Double maxWeight;
    private Double maxVolume;
}
