package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.persistence.model.Currency;
import java.math.BigDecimal;
import java.util.Set;

public interface ExchangeService {
    Set<Currency> getCurrencies();

    BigDecimal calculateExchangedValue(
            BigDecimal valueToAdd, ExchangeBidResponse exchangeBidResponse);

    double getBid(ExchangeBidResponse exchangeBidResponse);
}
