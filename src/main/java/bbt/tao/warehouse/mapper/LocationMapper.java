package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.location.LocationDTO;
    import bbt.tao.warehouse.model.Location;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.MappingTarget;

    import java.util.List;

    @Mapper(componentModel = "spring", uses = {WarehouseMapper.class})
    public interface LocationMapper {
        LocationDTO toDTO(Location location);

        List<LocationDTO> toDTOList(List<Location> locations);

        @Mapping(target = "warehouse", source = "warehouse")
        Location toEntity(LocationDTO dto);

        void updateEntityFromDTO(LocationDTO dto, @MappingTarget Location location);
    }