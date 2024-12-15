package br.com.sicredi.api.controller.v1;

import br.com.sicredi.api.controller.v1.dto.request.PollRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.PollDTO;
import br.com.sicredi.api.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService service;

    @GetMapping
    @ResponseStatus(OK)
    public Flux<PollDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(OK)
    public Mono<PollDTO> getPoll(@PathVariable("id") final UUID id) {
        return service.getPollDTO(id);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<PollDTO> createPoll(@Valid @RequestBody PollRequestDTO pollRequestDTO) {
        return service.save(pollRequestDTO);
    }

}