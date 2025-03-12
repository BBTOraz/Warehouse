package bbt.tao.warehouse.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private String unitOfMeasure;
    private Double minStockLevel;
    private String imageUrl;
    private Double weight;
    private Double volume;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer price;
}
