package com.example.demo.adaptor;

import com.example.demo.config.AppConfig;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.Currency;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Component
@RequiredArgsConstructor
public class NbpClient {

    private static final String EXCHANGE_URL =
            "exchangerates/rates/c/{currency}/{date}/?format=json";
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    public ExchangeBidResponse getExchangeValuesForCurrency(Currency currency) {
        LocalDate now = LocalDate.now();
        String mappedCurrency = currency.name().toLowerCase();
        String url = buildUrl(mappedCurrency, now);

        var response = restTemplate.getForObject(url, ExchangeBidResponse.class);

        if (response == null) {
            throw new TechnicalException("Can not get response from nbp");
        }

        return response;
    }

    private String buildUrl(String currency, LocalDate date) {
        String url = appConfig.getNpbBaseUrl() + EXCHANGE_URL;
        return new UriTemplate(url).expand(currency, date).toString();
    }
}
