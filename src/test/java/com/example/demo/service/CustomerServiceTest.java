package com.example.demo.service;

import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.persistence.model.Customer;
import com.example.demo.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.example.demo.common.TestFixtures.aCustomerWithBirthDate;
import static org.assertj.core.api.Assertions.assertThat;


import static com.example.demo.common.TestFixtures.aCustomer;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CustomerService customerService;

    @Test
    public void shouldFindByPesel() {
        var pesel = "123";
        var expected = aCustomer();
        doReturn(Optional.of(expected)).when(customerRepository).findByPesel(pesel);

        Customer result = customerService.findByPesel(pesel);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldSaveCustomer() {
        var customer = aCustomer();

        customerService.save(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    public void shouldThrowException_whenFindByPesel() {
        var pesel = "123";
        doReturn(Optional.empty()).when(customerRepository).findByPesel(pesel);

        assertThrows(
                ResourceNotFoundException.class, () -> customerService.findByPesel(pesel));
    }

    @Test
    public void shouldCreateCustomer() {
        var customer = aCustomer();

        customerService.createCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    public void shouldThrowException_whenAgeIsUnder18() {
        var customer = aCustomerWithBirthDate(LocalDate.of(2024, 1, 1));

        assertThatThrownBy(
                () -> customerService.createCustomer(customer))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerService.YOU_ARE_TOO_YOUNG_TO_CREATE_ACCOUNT);
    }
}
