package bbt.tao.warehouse.dto.inventory;


import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDTO {
    private Long id;
    private LocationDTO location;
    private ProductDTO product;
    private Double quantity;
    private String batchNumber;
    private String serialNumber;
    private String notes;
}
