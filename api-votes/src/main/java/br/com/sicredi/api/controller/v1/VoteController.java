package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.VoteRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VoteDTO;
import br.com.sicredi.api.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService service;

    @GetMapping
    @ResponseStatus(OK)
    public Flux<VoteDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(OK)
    public Mono<VoteDTO> getById(@PathVariable("id") final UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<VoteDTO> createVote(@Valid @RequestBody VoteRequestDTO request) {
        return service.save(request);
    }

}