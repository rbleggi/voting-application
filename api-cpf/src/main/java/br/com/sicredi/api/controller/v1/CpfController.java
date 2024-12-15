package br.com.sicredi.api.controller.v1;

import jakarta.validation.Valid;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.empty;

@RestController
@RequestMapping("v1/cpf")
public class CpfController {

    @GetMapping("{cpf}/validate")
    public Mono<Void> validate(@PathVariable @Valid @CPF String cpf) {
        return empty();
    }

}