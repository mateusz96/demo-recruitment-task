package com.example.demo.adaptor;

import java.util.List;
import lombok.Data;

@Data
public class ExchangeBidResponse {

    private String code;
    private List<Rate> rates;
}
