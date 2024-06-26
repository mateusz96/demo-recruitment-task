package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.Rate;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.Currency;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PlnExchangeService implements ExchangeService {

    @Override
    public Set<Currency> getCurrencies() {
        return Set.of(Currency.PLN);
    }

    @Override
    public BigDecimal calculateExchangedValue(
            BigDecimal valueToAdd, ExchangeBidResponse exchangeBidResponse) {
        double bid = getBid(exchangeBidResponse);

        return valueToAdd.multiply(BigDecimal.valueOf(bid));
    }

    @Override
    public double getBid(ExchangeBidResponse exchangeBidResponse) {
        return exchangeBidResponse.getRates().stream()
                .map(Rate::getAsk)
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }
}
