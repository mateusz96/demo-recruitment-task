package com.example.demo.util;

import com.example.demo.exceptions.BusinessException;
import java.sql.SQLException;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlExceptionHandler {
    public static final String UNIQUE_CONSTRAINT_SQL_CODE = "23505";

    public static final String PESEL_UNIQUE_CONSTRAINT = "customers_pesel_key";
    public static final String PESEL_UNIQUE_MESSAGE = "PESEL should be unique";

    private static final Map<String, String> CONSTRAINT_WITH_ERROR_MSG =
            Map.ofEntries(Map.entry(PESEL_UNIQUE_CONSTRAINT, PESEL_UNIQUE_MESSAGE));

    public static void handleUniqueConstraintViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof SQLException) {
            String state = ((SQLException) cause).getSQLState();
            if (UNIQUE_CONSTRAINT_SQL_CODE.equals(state)) {
                String message = cause.getMessage();
                CONSTRAINT_WITH_ERROR_MSG.forEach(
                        (key, value) -> {
                            if (message.contains(key)) {
                                throw new BusinessException(value);
                            }
                        });
            }
        }
    }
}
