package br.com.sicredi.api.config;

import br.com.sicredi.api.controller.v1.dto.response.ErrorResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static br.com.sicredi.api.util.Mapper.toErrorResponse;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class CustomExceptionHandler {

    public static final String OCORREU_UM_ERRO_AO_REALIZAR_A_OPERACAO = "Ocorreu um erro ao realizar a operação";

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(toErrorResponse(ex.getMessage(), OCORREU_UM_ERRO_AO_REALIZAR_A_OPERACAO)));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        return toErrorResponse(ex.getFieldErrors().stream()
                .map(error -> requireNonNullElse(error.getDefaultMessage(), OCORREU_UM_ERRO_AO_REALIZAR_A_OPERACAO))
                .collect(Collectors.joining(", ")), OCORREU_UM_ERRO_AO_REALIZAR_A_OPERACAO);
    }

}