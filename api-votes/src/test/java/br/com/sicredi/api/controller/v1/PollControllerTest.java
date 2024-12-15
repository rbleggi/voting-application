package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.PollRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.PollDTO;
import br.com.sicredi.api.service.PollService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static br.com.sicredi.api.utils.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PollController.class)
class PollControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PollService service;

    @Test
    void shouldSavePoll() {
        final var contractPath = "/contracts/controller/request/post-save-poll-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(
                requestJson,
                PollRequestDTO.class
        );

        when(service.save(request))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/v1/poll")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllPolls() {
        final var contractPath = "/contracts/controller/response/get-all-polls-response.json";

        final var responseFromJson = jsonToObject(
                contractPath,
                PollDTO.class
        );

        when(service.findAll())
                .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/poll")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetPollById() {
        final var contractPath = "/contracts/controller/response/get-poll-by-id-response.json";

        final var responseFromJson = jsonToObject(
                contractPath,
                PollDTO.class
        );

        when(service.getPollDTO(any()))
                .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/poll/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

}