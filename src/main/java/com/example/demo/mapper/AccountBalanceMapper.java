package com.example.demo.mapper;

import com.example.demo.api.model.AccountBalanceDto;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AccountBalanceMapper {

    public AccountBalanceDto mapToDto(AccountBalance accountBalance) {
        return AccountBalanceDto.builder()
                .value(accountBalance.getValue())
                .currency(accountBalance.getCurrency())
                .build();
    }

    public Set<AccountBalanceDto> mapToDtos(Set<AccountBalance> accountBalance) {
        return accountBalance.stream().map(this::mapToDto).collect(Collectors.toSet());
    }

    public Set<AccountBalance> buildNewAccountBalances(Customer customer) {
        Set<AccountBalance> accountBalances = new HashSet<>();
        accountBalances.add(buildNewAccountBalance(customer, Currency.USD));
        accountBalances.add(buildNewAccountBalance(customer, Currency.PLN));

        return accountBalances;
    }

    private AccountBalance buildNewAccountBalance(Customer customer, Currency currency) {
        return AccountBalance.builder()
                .customer(customer)
                .currency(currency)
                .value(BigDecimal.valueOf(0))
                .build();
    }
}
