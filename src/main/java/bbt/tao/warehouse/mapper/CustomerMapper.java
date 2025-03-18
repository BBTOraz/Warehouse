package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);

    List<CustomerDTO> toDTOList(List<Customer> customers);

    void updateEntityFromDTO(CustomerDTO dto, @MappingTarget Customer customer);

    Customer toEntity(CustomerDTO dto);
}