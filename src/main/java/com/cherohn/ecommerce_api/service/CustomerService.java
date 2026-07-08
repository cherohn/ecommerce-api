package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.request.CreateCustomerRequest;
import com.cherohn.ecommerce_api.exception.ResourceNotFoundException;
import com.cherohn.ecommerce_api.exception.ConflictException;
import com.cherohn.ecommerce_api.model.Customer;
import com.cherohn.ecommerce_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CreateCustomerRequest request){
        if(customerRepository.findByEmail(request.getEmail()).isPresent()){
            throw new ConflictException("Ja existe um cliente cadastrado com esse email");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return customerRepository.save(customer);
    }

    public Customer getById(Long id){
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));
    }
}
