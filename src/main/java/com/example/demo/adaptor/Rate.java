package com.example.demo.adaptor;

import java.time.LocalDate;
import lombok.*;

@Getter
public class Rate {

    private LocalDate effectiveDate;
    private double bid;
    private double ask;
}
