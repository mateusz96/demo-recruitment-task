package com.example.demo.api.model;

import com.example.demo.persistence.model.Currency;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountBalanceUpdateRequest {
    private Currency currencyFrom;
    private Currency currencyTo;

    @Min(value = 1, message = "Value cannot be less than 1")
    private BigDecimal value;
}
