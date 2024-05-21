package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.Rate;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ForeignCurrencyExchangeService implements ExchangeService {

    @Override
    public Set<Currency> getCurrencies() {
        return Set.of(Currency.USD);
    }

    @Override
    public BigDecimal calculateExchangedValue(
            BigDecimal valueToAdd, ExchangeBidResponse exchangeBidResponse) {
        double bid = getBid(exchangeBidResponse);

        return valueToAdd.divide(BigDecimal.valueOf(bid), 2, RoundingMode.DOWN);
    }

    @Override
    public double getBid(ExchangeBidResponse exchangeBidResponse) {
        return exchangeBidResponse.getRates().stream()
                .map(Rate::getBid)
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }
}
