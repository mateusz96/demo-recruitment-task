package com.example.demo.service;

import com.example.demo.adaptor.NbpClient;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.persistence.model.AccountBalance;
import com.example.demo.persistence.model.Currency;
import com.example.demo.persistence.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerAccountBalanceServiceTest {

    @Mock private CustomerService customerService;
    @Mock private NbpClient nbpClient;

    @Spy
    private ArrayList<ExchangeService> mockedExchangeServices;

    @InjectMocks
    private PlnExchangeService plnExchangeService;

    @InjectMocks
    private UsdExchangeService usdExchangeService;

    @BeforeEach
    public void setup() {
        mockedExchangeServices.add(plnExchangeService);
        mockedExchangeServices.add(usdExchangeService);
    }

    @Captor
    ArgumentCaptor<Customer> customerCaptor;

    @InjectMocks private CustomerAccountBalanceService service;

    @Test
    public void updateCustomerAccountBalance_whenExchangingPLNtoUSD() {
        var pesel = "123";
        var value = BigDecimal.valueOf(10);
        var expected = aCustomerWithAccountBalances();
        var exchangeBidResponse = anExchangeBidResponse();
        doReturn(expected).when(customerService).findByPesel(pesel);
        doReturn(exchangeBidResponse).when(nbpClient).getExchangeValuesForCurrency(Currency.USD);

        service.updateCustomerAccountBalance(pesel, Currency.PLN, Currency.USD, value);

        verify(customerService).save(customerCaptor.capture());
        var balances = customerCaptor.getValue().getAccountBalances().stream().collect(Collectors.toMap(AccountBalance::getCurrency, AccountBalance::getValue));
        assertThat(balances.get(Currency.USD)).isEqualByComparingTo(BigDecimal.valueOf(25));
        assertThat(balances.get(Currency.PLN)).isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    public void updateCustomerAccountBalance_whenExchangingUSDtoPLN() {
        var pesel = "123";
        var value = BigDecimal.valueOf(10);
        var expected = aCustomerWithAccountBalances();
        var exchangeBidResponse = anExchangeBidResponse();
        doReturn(expected).when(customerService).findByPesel(pesel);
        doReturn(exchangeBidResponse).when(nbpClient).getExchangeValuesForCurrency(Currency.USD);

        service.updateCustomerAccountBalance(pesel, Currency.USD, Currency.PLN, value);

        verify(customerService).save(customerCaptor.capture());
        var balances = customerCaptor.getValue().getAccountBalances().stream().collect(Collectors.toMap(AccountBalance::getCurrency, AccountBalance::getValue));
        assertThat(balances.get(Currency.USD)).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(balances.get(Currency.PLN)).isEqualByComparingTo(BigDecimal.valueOf(40));
    }

    @Test
    public void updateCustomerAccountBalance_shouldThrowException_whenCurrenciesAreSame() {
        assertThatThrownBy(
                () -> service.updateCustomerAccountBalance("123", Currency.USD, Currency.USD, BigDecimal.valueOf(10)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerAccountBalanceService.GIVEN_CURRIENCIES_ARE_THE_SAME);

    }

    @Test
    public void updateCustomerAccountBalance_shouldThrowException_whenNotEnoughMoney() {
        var pesel = "123";
        var value = BigDecimal.valueOf(30);
        var expected = aCustomerWithAccountBalances();
        doReturn(expected).when(customerService).findByPesel(pesel);

        assertThatThrownBy(
                () -> service.updateCustomerAccountBalance(pesel, Currency.PLN, Currency.USD, value))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerAccountBalanceService.YOU_DO_NOT_HAVE_ENOUGH_MONEY);
    }
}
