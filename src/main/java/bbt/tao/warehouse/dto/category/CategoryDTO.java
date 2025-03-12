package bbt.tao.warehouse.dto.category;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private CategorySummaryDTO parent;
    private List<CategorySummaryDTO> children;
}


