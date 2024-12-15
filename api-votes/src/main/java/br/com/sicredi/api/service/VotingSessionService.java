package br.com.sicredi.api.service;

import br.com.sicredi.api.controller.v1.dto.request.VotingSessionRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VotingSessionDTO;
import br.com.sicredi.api.domain.entity.PollEntity;
import br.com.sicredi.api.domain.entity.VoteEntity;
import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import br.com.sicredi.api.domain.enums.VoteEnum;
import br.com.sicredi.api.domain.repository.VoteRepository;
import br.com.sicredi.api.domain.repository.VotingSessionRepository;
import br.com.sicredi.api.messaging.VoteResultProducer;
import br.com.sicredi.api.messaging.messages.VoteResultMessage;
import br.com.sicredi.api.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static br.com.sicredi.api.domain.enums.SessionStatusEnum.CLOSED;
import static br.com.sicredi.api.domain.enums.SessionStatusEnum.OPENED;
import static br.com.sicredi.api.domain.enums.VoteEnum.NAO;
import static br.com.sicredi.api.domain.enums.VoteEnum.SIM;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final PollService pollService;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final TaskScheduler taskScheduler;
    private final VoteResultProducer voteResultProducer;

    public Flux<VotingSessionDTO> findAll() {
        return votingSessionRepository.findAll()
                .map(Mapper::toVotingSessionDTO)
                .switchIfEmpty(Mono.empty());
    }

    public Mono<VotingSessionDTO> getVotingSession(final UUID id) {
        return votingSessionRepository.findById(id)
                .map(Mapper::toVotingSessionDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Não foi possível localizar nenhum registro para o id: %s".formatted(id))));
    }

    public Mono<VotingSessionDTO> save(final VotingSessionRequestDTO request) {
        return pollService.getPollEntity(request.pollId())
                .doOnNext(pollEntity -> log.info("Received data to open a new voting session with poll id: {}", request.pollId()))
                .doOnNext(this::validateSession)
                .flatMap(pollEntity -> Mono.zip(saveAndUpdateVotingSession(request, pollEntity), Mono.just(pollEntity)))
                .doOnNext(tuple -> taskScheduler.schedule(() ->
                        closeVoteSessionSchedule(tuple.getT1(), tuple.getT2().getId()), Instant.now().plus(request.duration())))
                .map(tuple -> VotingSessionDTO.builder().id(tuple.getT1().getId()).build());
    }

    private void validateSession(final PollEntity pollEntity) {
        ofNullable(pollEntity.getVotingSessionId()).ifPresent(uuid -> {
            throw new ResponseStatusException(BAD_REQUEST, "Já existe uma sessão criada para esta pauta");
        });
    }

    private Mono<VotingSessionEntity> saveAndUpdateVotingSession(final VotingSessionRequestDTO request, final PollEntity pollEntity) {
        return votingSessionRepository.save(VotingSessionEntity.builder().status(OPENED).duration(request.duration()).build())
                .doOnNext(votingSessionEntity -> pollService.save(pollEntity.withVotingSessionId(votingSessionEntity.getId()).withUpdatedAt(now())).subscribe())
                .doOnError(ex -> {
                    log.error("Error creating voting session with poll id: {}", request.pollId());
                    throw new ResponseStatusException(BAD_REQUEST, "Ocorreu um erro ao tentar criar uma sessão de votos para a pauta: %s".formatted(pollEntity.getName()));
                });
    }

    private void closeVoteSessionSchedule(final VotingSessionEntity votingSessionEntity, final UUID pollId) {
        log.info("Closing voting session with id: {}", votingSessionEntity.getId());
        pollService.getPollEntity(pollId)
                .flatMap(pollEntity -> Mono.zip(Mono.just(votingSessionEntity), Mono.just(pollEntity)))
                .doOnNext(tuple -> closeVotingSession(tuple.getT1()).subscribe())
                .doOnNext(tuple -> voteRepository.findAllByVotingSessionId(tuple.getT1().getId()).collectList()
                        .flatMap(voteEntities -> updatePollWithVotes(tuple.getT2(), voteEntities).thenReturn(voteEntities))
                        .doOnSuccess(voteEntities -> sendMessage(votingSessionEntity, voteEntities))
                        .subscribe())
                .subscribe();
    }

    private Mono<VotingSessionEntity> closeVotingSession(VotingSessionEntity voteSessionEntity) {
        return votingSessionRepository.save(voteSessionEntity.withStatus(CLOSED).withClosedDate(now()));
    }

    private Mono<PollEntity> updatePollWithVotes(final PollEntity poll, final List<VoteEntity> voteEntities) {
        return pollService.save(poll
                .withTotalVotes(voteEntities.size())
                .withVotesAgainst(getVotes(voteEntities, NAO))
                .withVotesInFavour(getVotes(voteEntities, SIM))
                .withUpdatedAt(now()));
    }

    private void sendMessage(final VotingSessionEntity votingSessionEntity, final List<VoteEntity> voteEntities) {
        voteResultProducer.send(VoteResultMessage.builder()
                .votingSessionId(votingSessionEntity.getId())
                .totalVotes(voteEntities.size())
                .votesInFavour(getVotes(voteEntities, SIM))
                .votesAgainst(getVotes(voteEntities, NAO))
                .build());
    }

    private static int getVotes(final List<VoteEntity> voteEntities, final VoteEnum voteEnum) {
        return voteEntities.stream()
                .filter(voteEntity -> voteEnum.equals(voteEntity.getVoteEnum()))
                .toList().size();
    }

}