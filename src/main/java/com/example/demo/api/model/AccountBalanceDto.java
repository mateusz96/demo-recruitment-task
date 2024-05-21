package com.example.demo.api.model;

import com.example.demo.persistence.model.Currency;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceDto {
    private Currency currency;
    private BigDecimal value;
}
