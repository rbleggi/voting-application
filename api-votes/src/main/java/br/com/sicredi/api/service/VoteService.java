package br.com.sicredi.api.service;

import br.com.sicredi.api.client.CpfValidatorClient;
import br.com.sicredi.api.controller.v1.dto.request.VoteRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VoteDTO;
import br.com.sicredi.api.domain.entity.VoteEntity;
import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import br.com.sicredi.api.domain.repository.VoteRepository;
import br.com.sicredi.api.domain.repository.VotingSessionRepository;
import br.com.sicredi.api.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static br.com.sicredi.api.domain.enums.SessionStatusEnum.CLOSED;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final CpfValidatorClient client;

    public Flux<VoteDTO> findAll() {
        return voteRepository.findAll()
                .map(Mapper::toVoteDTO)
                .switchIfEmpty(Mono.empty());
    }

    public Mono<VoteDTO> getById(final UUID id) {
        return voteRepository.findById(id)
                .map(Mapper::toVoteDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }

    public Mono<VoteDTO> save(final VoteRequestDTO request) {
        return votingSessionRepository.findById(request.votingSessionId())
                .doOnNext(votingSessionEntity -> log.info("Received data to submit vote with voting session id: {}", votingSessionEntity.getId()))
                .flatMap(votingSessionEntity -> validateIsAbleToVote(request.cpf(), votingSessionEntity))
                .flatMap(this::validateVotingSession)
                .flatMap(votingSessionEntity -> saveVote(request, votingSessionEntity)
                )
                .map(voteEntity -> VoteDTO.builder().id(voteEntity.getId()).build());
    }

    private Mono<VoteEntity> saveVote(final VoteRequestDTO request, final VotingSessionEntity votingSessionEntity) {
        return voteRepository.save(VoteEntity.builder()
                        .voteEnum(request.vote())
                        .votingSessionId(votingSessionEntity.getId())
                        .cpf(request.cpf())
                        .build())
                .doOnError(DuplicateKeyException.class, ex -> {
                    log.error("Error creating vote: {}", request.vote());
                    throw new ResponseStatusException(BAD_REQUEST, "Este cpf já possui um voto registrado");
                });
    }

    private Mono<VotingSessionEntity> validateIsAbleToVote(final String cpf, final VotingSessionEntity votingSessionEntity) {
        return client.validateCpf(cpf)
                .onErrorMap(ex -> {
                    throw new ResponseStatusException(NOT_FOUND, "CPF Inválido");
                }).thenReturn(votingSessionEntity);
    }

    private Mono<VotingSessionEntity> validateVotingSession(final VotingSessionEntity votingSessionEntity) {
        ofNullable(votingSessionEntity)
                .map(VotingSessionEntity::getStatus)
                .filter(CLOSED::equals)
                .ifPresent(sessionStatusEnum -> {
                    throw new ResponseStatusException(BAD_REQUEST, "Essa sessão já foi fechada e não está mais disponível para votos");
                });
        return Mono.just(votingSessionEntity);
    }

}