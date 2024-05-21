package com.example.demo.service;

import static com.example.demo.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.example.demo.adaptor.ExchangeBidResponse;
import com.example.demo.adaptor.NbpClient;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerAccountBalanceServiceTest {

    private String pesel;
    private BigDecimal value;
    private BigDecimal entryValue;
    private BigDecimal calculatedExchangeBid;
    private ExchangeBidResponse exchangeBidResponse;

    @Mock private CustomerService customerService;
    @Mock private NbpClient nbpClient;

    @Spy private ArrayList<ExchangeService> mockedExchangeServices;
    @Mock private PlnExchangeService plnExchangeService;
    @Mock private ForeignCurrencyExchangeService foreignCurrencyExchangeService;

    @BeforeEach
    public void setup() {
        mockedExchangeServices.add(plnExchangeService);
        mockedExchangeServices.add(foreignCurrencyExchangeService);
        pesel = "123";
        value = BigDecimal.valueOf(10);
        entryValue = BigDecimal.valueOf(20);
        calculatedExchangeBid = BigDecimal.valueOf(5);
        exchangeBidResponse = anExchangeBidResponse();
    }

    @Captor ArgumentCaptor<Customer> customerCaptor;

    @InjectMocks private CustomerAccountBalanceService service;

    @Test
    public void updateCustomerAccountBalance_whenExchangingPLNtoUSD() {
        var expected = aCustomerWithAccountBalances(entryValue);
        doReturn(expected).when(customerService).findByPesel(pesel);
        doReturn(exchangeBidResponse).when(nbpClient).getExchangeValuesForCurrency(Currency.USD);
        doReturn(calculatedExchangeBid)
                .when(foreignCurrencyExchangeService)
                .calculateExchangedValue(value, exchangeBidResponse);
        doReturn(Set.of(Currency.USD)).when(foreignCurrencyExchangeService).getCurrencies();

        service.updateCustomerAccountBalance(pesel, Currency.PLN, Currency.USD, value);

        verify(customerService).save(customerCaptor.capture());
        var balances =
                customerCaptor.getValue().getAccountBalances().stream()
                        .collect(
                                Collectors.toMap(
                                        AccountBalance::getCurrency, AccountBalance::getValue));
        assertThat(balances.get(Currency.USD))
                .isEqualByComparingTo(entryValue.add(calculatedExchangeBid));
        assertThat(balances.get(Currency.PLN)).isEqualByComparingTo(entryValue.subtract(value));
    }

    @Test
    public void updateCustomerAccountBalance_whenExchangingUSDtoPLN() {
        var expected = aCustomerWithAccountBalances(entryValue);
        doReturn(expected).when(customerService).findByPesel(pesel);
        doReturn(exchangeBidResponse).when(nbpClient).getExchangeValuesForCurrency(Currency.USD);
        doReturn(calculatedExchangeBid)
                .when(plnExchangeService)
                .calculateExchangedValue(value, exchangeBidResponse);
        doReturn(Set.of(Currency.PLN)).when(plnExchangeService).getCurrencies();

        service.updateCustomerAccountBalance(pesel, Currency.USD, Currency.PLN, value);

        verify(customerService).save(customerCaptor.capture());
        var balances =
                customerCaptor.getValue().getAccountBalances().stream()
                        .collect(
                                Collectors.toMap(
                                        AccountBalance::getCurrency, AccountBalance::getValue));
        assertThat(balances.get(Currency.USD)).isEqualByComparingTo(entryValue.subtract(value));
        assertThat(balances.get(Currency.PLN))
                .isEqualByComparingTo(entryValue.add(calculatedExchangeBid));
    }

    @Test
    public void updateCustomerAccountBalance_shouldThrowException_whenCurrenciesAreSame() {
        assertThatThrownBy(
                        () ->
                                service.updateCustomerAccountBalance(
                                        "123", Currency.USD, Currency.USD, BigDecimal.valueOf(10)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerAccountBalanceService.GIVEN_CURRIENCIES_ARE_THE_SAME);
    }

    @Test
    public void updateCustomerAccountBalance_shouldThrowException_whenNotEnoughMoney() {
        var value = BigDecimal.valueOf(30);
        var expected = aCustomerWithAccountBalances(entryValue);
        doReturn(expected).when(customerService).findByPesel(pesel);

        assertThatThrownBy(
                        () ->
                                service.updateCustomerAccountBalance(
                                        pesel, Currency.PLN, Currency.USD, value))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerAccountBalanceService.YOU_DO_NOT_HAVE_ENOUGH_MONEY);
    }
}
