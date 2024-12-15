package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.VoteRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VoteDTO;
import br.com.sicredi.api.service.VoteService;
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
@WebFluxTest(controllers = VoteController.class)
class VoteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private VoteService service;

    @Test
    void shouldSaveVote() {
        final var contractPath = "/contracts/controller/request/post-save-vote-request.json";

        final var requestJson = jsonFromFile(contractPath);
        final var request = jsonStrToObject(requestJson, VoteRequestDTO.class);

        when(service.save(request))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/v1/vote")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldFindAllVotes() {
        final var contractPath = "/contracts/controller/response/get-all-votes-response.json";

        final var responseFromJson = jsonToObject(contractPath, VoteDTO.class);

        when(service.findAll())
                .thenReturn(Flux.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/vote")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

    @Test
    void shouldGetVoteById() throws Exception {
        final var contractPath = "/contracts/controller/response/get-vote-by-id-response.json";

        final var responseFromJson = jsonToObject(
                contractPath,
                VoteDTO.class
        );

        when(service.getById(any()))
                .thenReturn(Mono.just(responseFromJson));

        webTestClient.get()
                .uri("/v1/vote/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(contractPath);
    }

}