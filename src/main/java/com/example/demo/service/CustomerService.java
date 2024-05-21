package com.example.demo.service;

import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.persistence.model.Customer;
import com.example.demo.persistence.repository.CustomerRepository;
import com.example.demo.util.SqlExceptionHandler;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public static final String YOU_ARE_TOO_YOUNG_TO_CREATE_ACCOUNT =
            "You are too young to create account";
    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        validateBirthDate(customer.getBirthDate());
        return save(customer);
    }

    public Customer findByPesel(String pesel) {
        return customerRepository.findByPesel(pesel).orElseThrow(ResourceNotFoundException::new);
    }

    public Customer save(Customer customer) {
        Customer savedCustomer;
        try {
            savedCustomer = customerRepository.save(customer);
        } catch (DataIntegrityViolationException ex) {
            SqlExceptionHandler.handleUniqueConstraintViolation(ex);
            throw new BusinessException("Something went wrong");
        }

        return savedCustomer;
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (ChronoUnit.YEARS.between(birthDate, LocalDate.now()) < 18) {
            throw new BusinessException(YOU_ARE_TOO_YOUNG_TO_CREATE_ACCOUNT);
        }
    }
}
