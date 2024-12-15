package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.VotingSessionRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VotingSessionDTO;
import br.com.sicredi.api.service.VotingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/voting-session")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService service;

    @GetMapping
    @ResponseStatus(OK)
    public Flux<VotingSessionDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(OK)
    public Mono<VotingSessionDTO> getById(@PathVariable("id") final UUID id) {
        return service.getVotingSession(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<VotingSessionDTO> createVotingSession(@Valid @RequestBody VotingSessionRequestDTO request) {
        return service.save(request);
    }

}