package br.com.sicredi.api.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "CpfValidatorClient", url = "${api.client.cpf.v1.host}")
public interface CpfValidatorClient {

    @GetMapping("${api.client.cpf.v1.validate}")
    Mono<Void> validateCpf(@RequestParam final String cpf);

}