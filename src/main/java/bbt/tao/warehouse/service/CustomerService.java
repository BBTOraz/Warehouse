package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.customer.CustomerDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {

    List<CustomerDTO> findAllCustomers();

    List<CustomerDTO> findAllActiveCustomers();

    Optional<CustomerDTO> findCustomerById(Long id);

    List<CustomerDTO> findCustomersByName(String name);

    Optional<CustomerDTO> findCustomerByTaxId(String taxId);

    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long id);
}
