package com.example.demo.service;

import static com.example.demo.common.TestFixtures.aCustomer;
import static com.example.demo.common.TestFixtures.aCustomerWithBirthDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.persistence.model.Customer;
import com.example.demo.persistence.repository.CustomerRepository;
import com.example.demo.util.SqlExceptionHandler;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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
    public void shouldThrowException_whenSavingCustomerWithSamePesel() {
        var uniqueKey = "customers_pesel_key";
        var customer = aCustomer();
        Throwable sqlException = new SQLException(uniqueKey, "23505");
        Exception exception = new DataIntegrityViolationException(uniqueKey, sqlException);
        doThrow(exception).when(customerRepository).save(customer);

        assertThatThrownBy(() -> customerService.save(customer))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SqlExceptionHandler.PESEL_UNIQUE_MESSAGE);
    }

    @Test
    public void shouldThrowException_whenFindByPesel() {
        var pesel = "123";
        doReturn(Optional.empty()).when(customerRepository).findByPesel(pesel);

        assertThrows(ResourceNotFoundException.class, () -> customerService.findByPesel(pesel));
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

        assertThatThrownBy(() -> customerService.createCustomer(customer))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CustomerService.AGE_CONDITION_EXCEPTION_MESSAGE);
    }
}
