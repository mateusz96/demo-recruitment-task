package com.example.demo.common;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.Rate;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static Customer aCustomerWithAccountBalances(BigDecimal accountBalanceValue) {
        var customer =
                Customer.builder()
                        .id(UUID.randomUUID())
                        .pesel("123")
                        .firstName("FirstName")
                        .lastName("LastName")
                        .birthDate(LocalDate.of(1900, 1, 1))
                        .build();
        var balances = anAccountBalances(customer, accountBalanceValue);
        customer.setAccountBalances(balances);

        return customer;
    }

    public static AccountBalance anAccountBalance(
            Customer customer, Currency currency, BigDecimal value) {
        return AccountBalance.builder()
                .customer(customer)
                .currency(currency)
                .value(value)
                .id(UUID.randomUUID())
                .build();
    }

    public static Set<AccountBalance> anAccountBalances(Customer customer, BigDecimal value) {
        Set<AccountBalance> accountBalances = new HashSet<>();
        accountBalances.add(anAccountBalance(customer, Currency.PLN, value));
        accountBalances.add(anAccountBalance(customer, Currency.USD, value));

        return accountBalances;
    }

    public static ExchangeBidResponse anExchangeBidResponse() {
        return ExchangeBidResponse.builder()
                .code("usd")
                .rates(List.of(new Rate(LocalDate.now(), 2, 2)))
                .build();
    }
}
