package br.com.sicredi.api.controller.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(CpfController.class)
public class CpfValidatorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnTrueForValidCpf() {
        webTestClient.get()
                .uri("/v1/cpf/{cpf}/validate", "123.456.789-09")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnBadRequestForInvalidCpf() {
        webTestClient.get()
                .uri("/v1/cpf/{cpf}/validate", "123")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

}