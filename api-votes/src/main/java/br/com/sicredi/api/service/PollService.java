package br.com.sicredi.api.service;

import br.com.sicredi.api.controller.v1.dto.request.PollRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.PollDTO;
import br.com.sicredi.api.domain.entity.PollEntity;
import br.com.sicredi.api.domain.repository.PollRepository;
import br.com.sicredi.api.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository repository;

    public Flux<PollDTO> findAll() {
        return repository.findAll().map(Mapper::toPollDTO).switchIfEmpty(Mono.empty());
    }

    public Mono<PollEntity> getPollEntity(final UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }

    public Mono<PollDTO> getPollDTO(final UUID id) {
        return getPollEntity(id).map(Mapper::toPollDTO);
    }

    public Mono<PollDTO> save(final PollRequestDTO pollRequestDTO) {
        return repository.save(PollEntity.builder()
                        .name(pollRequestDTO.name())
                        .description(pollRequestDTO.description())
                        .build())
                .doOnError(ex -> {
                    log.error(ex.getMessage(), ex);
                    throw new ResponseStatusException(BAD_REQUEST, "Ocorreu um erro ao tentar salvar a pauta");
                }).map(pollEntity -> PollDTO.builder().id(pollEntity.getId()).build());
    }

    public Mono<PollEntity> save(final PollEntity pollEntity) {
        return repository.save(pollEntity)
                .doOnError(ex -> {
                    log.error(ex.getMessage(), ex);
                    throw new ResponseStatusException(BAD_REQUEST, "Ocorreu um erro ao tentar salvar a pauta");
                });
    }

}