package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOList(List<Product> products);

    @Mapping(target = "category", source = "category")
    Product toEntity(ProductDTO dto);

    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product product);
}