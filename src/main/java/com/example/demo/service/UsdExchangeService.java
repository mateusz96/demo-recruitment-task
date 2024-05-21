package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.Rate;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class UsdExchangeService implements ExchangeService {

    @Override
    public Currency getCurrency() {
        return Currency.USD;
    }

    @Override
    public void exchange(
            Customer customer,
            AccountBalance actualBalance,
            BigDecimal value,
            ExchangeBidResponse exchangeBidResponse) {
        double bid = getBid(exchangeBidResponse);

        actualBalance.setValue(actualBalance.getValue().subtract(value));
        AccountBalance updatedBalance = getAccountBalanceByCurrency(customer);
        updatedBalance.setValue(
                updatedBalance
                        .getValue()
                        .add(value.divide(BigDecimal.valueOf(bid), 2, RoundingMode.DOWN)));
    }

    @Override
    public double getBid(ExchangeBidResponse exchangeBidResponse) {
        return exchangeBidResponse.getRates().stream()
                .map(Rate::getBid)
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }
}
