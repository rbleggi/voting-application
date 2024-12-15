package br.com.sicredi.api.controller.v1.error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static reactor.core.publisher.Mono.empty;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(NOT_FOUND)
    public Mono<Void> handleInvalidCpf(HandlerMethodValidationException ex) {
        return empty();
    }

}