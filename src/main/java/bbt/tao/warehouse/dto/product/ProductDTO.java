package bbt.tao.warehouse.dto.product;

import bbt.tao.warehouse.dto.category.CategorySummaryDTO;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private CategorySummaryDTO category;
    private String unitOfMeasure;
    private Double minStockLevel;
    private String imageUrl;
    private Double weight;
    private Double volume;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer price;
}


