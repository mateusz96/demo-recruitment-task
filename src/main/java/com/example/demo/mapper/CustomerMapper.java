package com.example.demo.mapper;

import com.example.demo.api.model.CustomerCreateRequest;
import com.example.demo.api.model.CustomerDto;
import com.example.demo.persistence.model.Customer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final AccountBalanceMapper accountBalanceMapper;

    public Customer mapToEntity(CustomerCreateRequest request) {
        var customer =
                Customer.builder()
                        .id(UUID.randomUUID())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .pesel(request.getPesel())
                        .birthDate(request.getBirthDate())
                        .build();
        customer.setAccountBalances(accountBalanceMapper.buildNewAccountBalances(customer));

        return customer;
    }

    public CustomerDto mapToDto(Customer customer) {
        return CustomerDto.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .pesel(customer.getPesel())
                .birthDate(customer.getBirthDate())
                .accountBalances(accountBalanceMapper.mapToDtos(customer.getAccountBalances()))
                .build();
    }
}
