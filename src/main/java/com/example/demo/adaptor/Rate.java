package com.example.demo.adaptor;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rate {

    private LocalDate effectiveDate;
    private double bid;
    private double ask;
}
