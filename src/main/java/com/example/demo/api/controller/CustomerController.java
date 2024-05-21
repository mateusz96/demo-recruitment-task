package com.example.demo.api.controller;

import com.example.demo.api.model.CustomerAccountBalanceUpdateRequest;
import com.example.demo.api.model.CustomerCreateRequest;
import com.example.demo.api.model.CustomerDto;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.persistence.model.Customer;
import com.example.demo.service.CustomerAccountBalanceService;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerAccountBalanceService customerAccountBalanceService;

    @GetMapping("/{pesel}")
    public ResponseEntity<CustomerDto> findByPesel(@PathVariable final String pesel) {
        Customer customer = customerService.findByPesel(pesel);
        CustomerDto dto = customerMapper.mapToDto(customer);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request) {

        Customer customer = customerMapper.mapToEntity(request);
        Customer savedCustomer = customerService.createCustomer(customer);
        CustomerDto dto = customerMapper.mapToDto(savedCustomer);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PatchMapping("/{pesel}/account-balance")
    public ResponseEntity<CustomerDto> updateAccountBalance(
            @PathVariable final String pesel,
            @Valid @RequestBody CustomerAccountBalanceUpdateRequest request) {
        Customer customer =
                customerAccountBalanceService.updateCustomerAccountBalance(
                        pesel,
                        request.getCurrencyFrom(),
                        request.getCurrencyTo(),
                        request.getValue());
        CustomerDto customerDto = customerMapper.mapToDto(customer);
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }
}
