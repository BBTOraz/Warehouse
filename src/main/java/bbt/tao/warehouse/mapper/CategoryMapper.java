package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.category.CategoryDTO;
import bbt.tao.warehouse.dto.category.CategorySummaryDTO;
import bbt.tao.warehouse.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parent", source = "parent", qualifiedByName = "toSummary")
    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO dto);

    @Named("toSummary")
    default CategorySummaryDTO toSummary(Category category) {
        if (category == null) return null;
        return CategorySummaryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}