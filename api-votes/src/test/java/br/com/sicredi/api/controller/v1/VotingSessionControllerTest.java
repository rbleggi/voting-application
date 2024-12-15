package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.VotingSessionRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VotingSessionDTO;
import br.com.sicredi.api.service.VotingSessionService;
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
@WebFluxTest(controllers = VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private VotingSessionService service;

    @Test
    void shouldSaveVotingSession() throws Exception {
        final var contractPath = "/contracts/controller/request/post-save-voting-session-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(
                requestJson,
                VotingSessionRequestDTO.class
        );

        when(service.save(request))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/v1/voting-session")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllVotingSessions() throws Exception {
        final var contractPath = "/contracts/controller/response/get-all-voting-sessions-response.json";

        final var responseFromJson = jsonToObject(
                contractPath,
                VotingSessionDTO.class
        );

        when(service.findAll())
                .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/voting-session")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetVotingSessionById() throws Exception {
        final var contractPath = "/contracts/controller/response/get-voting-session-by-id-response.json";

        final var responseFromJson = jsonToObject(
                contractPath,
                VotingSessionDTO.class
        );

        when(service.getVotingSession(any()))
                .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/voting-session/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

}