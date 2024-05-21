package com.example.demo.service;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.NbpClient;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.TechnicalException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerAccountBalanceService {

    public static final String GIVEN_CURRIENCIES_ARE_THE_SAME = "Given curriencies are the same";
    public static final String YOU_DO_NOT_HAVE_ENOUGH_MONEY = "You do not have enough money";
    private final CustomerService customerService;
    private final List<ExchangeService> exchangeServices;
    private final NbpClient nbpClient;

    @Transactional
    public Customer updateCustomerAccountBalance(
            String pesel, Currency currencyFrom, Currency currencyTo, BigDecimal value) {
        validateCurrencies(currencyFrom, currencyTo);

        Customer customer = customerService.findByPesel(pesel);
        AccountBalance actualBalance = getAccountBalanceByCurrency(customer, currencyFrom);
        validateAccountBalance(actualBalance, value);
        ExchangeBidResponse exchangeBidResponse = getValueToExchange(currencyFrom, currencyTo);

        findExchangeService(currencyTo)
                .exchange(customer, actualBalance, value, exchangeBidResponse);

        return customerService.save(customer);
    }

    private AccountBalance getAccountBalanceByCurrency(Customer customer, Currency currency) {
        return customer.getAccountBalances().stream()
                .filter(it -> it.getCurrency() == currency)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateAccountBalance(AccountBalance balance, BigDecimal value) {
        if (balance.getValue().compareTo(value) < 0) {
            throw new BusinessException(YOU_DO_NOT_HAVE_ENOUGH_MONEY);
        }
    }

    private void validateCurrencies(Currency currencyFrom, Currency currencyTo) {
        if (currencyFrom == currencyTo) {
            throw new BusinessException(GIVEN_CURRIENCIES_ARE_THE_SAME);
        }
    }

    private ExchangeService findExchangeService(Currency currencyTo) {
        return exchangeServices.stream()
                .filter(it -> it.getCurrency() == currencyTo)
                .findFirst()
                .orElseThrow(TechnicalException::new);
    }

    private ExchangeBidResponse getValueToExchange(Currency currencyFrom, Currency currencyTo) {
        Currency currency =
                Stream.of(currencyFrom, currencyTo)
                        .filter(it -> it != Currency.PLN)
                        .findFirst()
                        .orElseThrow(TechnicalException::new);
        return nbpClient.getExchangeValuesForCurrency(currency);
    }
}
