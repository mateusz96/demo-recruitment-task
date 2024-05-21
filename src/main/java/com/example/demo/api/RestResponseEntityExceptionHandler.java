package com.example.demo.api;

import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.TechnicalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class RestResponseEntityExceptionHandler implements ProblemHandling {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Problem> handleNotFound(
            final RuntimeException ex, final NativeWebRequest request) {
        Problem problem = buildProblem(ex.getMessage(), Status.NOT_FOUND);
        return create(ex, problem, request);
    }

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Problem> handleBadRequest(
            final RuntimeException ex, final NativeWebRequest request) {
        Problem problem = buildProblem(ex.getMessage(), Status.BAD_REQUEST);
        return create(ex, problem, request);
    }

    @ExceptionHandler(value = {TechnicalException.class})
    protected ResponseEntity<Problem> handleInternalServerError(
            final RuntimeException ex, final NativeWebRequest request) {
        Problem problem = buildProblem("Internal Server Error", Status.INTERNAL_SERVER_ERROR);
        return create(ex, problem, request);
    }

    private Problem buildProblem(String detail, Status status) {
        return Problem.builder().withStatus(status).withDetail(detail).build();
    }
}
