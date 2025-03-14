package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.mapper.CustomerMapper;
import bbt.tao.warehouse.model.Customer;
import bbt.tao.warehouse.repository.CustomerRepository;
import bbt.tao.warehouse.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDTO> findAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toDTOList(customers);
    }

    @Override
    public List<CustomerDTO> findAllActiveCustomers() {
        List<Customer> activeCustomers = customerRepository.findByIsActiveTrue();
        return customerMapper.toDTOList(activeCustomers);
    }

    @Override
    public Optional<CustomerDTO> findCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDTO);
    }

    @Override
    public List<CustomerDTO> findCustomersByName(String name) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name);
        return customerMapper.toDTOList(customers);
    }

    @Override
    public Optional<CustomerDTO> findCustomerByTaxId(String taxId) {
        return customerRepository.findByTaxId(taxId)
                .map(customerMapper::toDTO);
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer;

        if (customerDTO.getId() != null) {
            Optional<Customer> existingCustomer = customerRepository.findById(customerDTO.getId());
            if (existingCustomer.isPresent()) {
                customer = existingCustomer.get();
                customerMapper.updateEntityFromDTO(customerDTO, customer);
            } else {
                customer = customerMapper.toEntity(customerDTO);
            }
        } else {
            customer = customerMapper.toEntity(customerDTO);
        }

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}