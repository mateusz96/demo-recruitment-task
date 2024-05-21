package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.Rate;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class PlnExchangeService implements ExchangeService {

    @Override
    public Currency getCurrency() {
        return Currency.PLN;
    }

    @Override
    public void exchange(
            Customer customer,
            AccountBalance actualBalance,
            BigDecimal value,
            ExchangeBidResponse exchangeBidResponse) {
        double bid = getBid(exchangeBidResponse);

        actualBalance.setValue(actualBalance.getValue().subtract(value));
        AccountBalance toBe = getAccountBalanceByCurrency(customer);
        toBe.setValue(toBe.getValue().add(value.multiply(BigDecimal.valueOf(bid))));
    }

    @Override
    public double getBid(ExchangeBidResponse exchangeBidResponse) {
        return exchangeBidResponse.getRates().stream()
                .map(Rate::getAsk)
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }
}
