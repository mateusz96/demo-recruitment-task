package com.example.demo.common;

import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

public class TestFixtures {

    public static Customer aCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .pesel("123")
                .firstName("FirstName")
                .lastName("LastName")
                .birthDate(LocalDate.of(1900, 1, 1))
                .accountBalances(new HashSet<>())
                .build();

    }

    public static Customer aCustomerWithBirthDate(LocalDate date) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .pesel("123")
                .firstName("FirstName")
                .lastName("LastName")
                .birthDate(date)
                .build();
    }
}
