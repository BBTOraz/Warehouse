package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Customer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {

    List<Customer> findAllCustomers();

    List<Customer> findAllActiveCustomers();

    Optional<Customer> findCustomerById(Long id);

    List<Customer> findCustomersByName(String name);

    Optional<Customer> findCustomerByTaxId(String taxId);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);
}
