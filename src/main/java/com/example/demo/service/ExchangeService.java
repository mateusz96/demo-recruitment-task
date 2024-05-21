package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;

public interface ExchangeService {

    default AccountBalance getAccountBalanceByCurrency(Customer customer) {
        return customer.getAccountBalances().stream()
                .filter(it -> it.getCurrency() == getCurrency())
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }

    Currency getCurrency();

    void exchange(
            Customer customer,
            AccountBalance actualBalance,
            BigDecimal value,
            ExchangeBidResponse exchangeBidResponse);

    double getBid(ExchangeBidResponse exchangeBidResponse);
}
