package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class RestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(new JavaTimeModule(), new ConstraintViolationProblemModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(FAIL_ON_NULL_FOR_PRIMITIVES)
                .enable(WRITE_BIGDECIMAL_AS_PLAIN)
                .disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
