package com.bank.bankApp.services;

import com.bank.bankApp.dtos.CustomerDetailResponse;
import com.bank.bankApp.dtos.CustomerRequest;
import com.bank.bankApp.dtos.CustomerResponse;
import com.bank.bankApp.entity.Customer;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.mapper.CustomerMapper;
import com.bank.bankApp.repository.CustomerRepository;
import com.bank.bankApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    public CustomerResponse addCustomer(Long userId, CustomerRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        Customer customer = CustomerMapper.toEntity(request);
        customer.setUser(user);

        return CustomerMapper.toDTO(customerRepository.save(customer));
    }

    public CustomerResponse getCustomer(Long userId) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for userId " + userId));

        return CustomerMapper.toDTO(customer);
    }

    public void deleteCustomer(Long userId) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for userId " + userId));

        customerRepository.delete(customer);
    }

    public CustomerResponse updateCustomer(Long userId, CustomerRequest request) {
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for userId " + userId));


        if (request.getPhoneNo() != null && !request.getPhoneNo().isEmpty()) {
            customer.setPhoneNo(request.getPhoneNo());
        }
        if (request.getAge() != 0) {
            customer.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            customer.setGender(request.getGender());
        }

        return CustomerMapper.toDTO(customerRepository.save(customer));
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }


//    public CustomerService(CustomerRepository customerRepository) {
//        this.customerRepository = customerRepository;
//    }

    public CustomerDetailResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        User user = customer.getUser();

        return new CustomerDetailResponse(
                customer.getCustomerId(),
                customer.getPhoneNo(),
                customer.getAge(),
                customer.getGender() != null ? customer.getGender().name() : null,
                user.getName(),
                user.getEmail()
        );
    }


}
