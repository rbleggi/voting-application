package br.com.sicredi.api.service;

import br.com.sicredi.api.client.CpfValidatorClient;
import br.com.sicredi.api.controller.v1.dto.request.VoteRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VoteDTO;
import br.com.sicredi.api.domain.entity.VoteEntity;
import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import br.com.sicredi.api.domain.repository.VoteRepository;
import br.com.sicredi.api.domain.repository.VotingSessionRepository;
import br.com.sicredi.api.service.VoteService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static br.com.sicredi.api.domain.enums.SessionStatusEnum.CLOSED;
import static br.com.sicredi.api.domain.enums.SessionStatusEnum.OPENED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.empty;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @InjectMocks
    private VoteService service;
    @Mock
    private VotingSessionRepository votingSessionRepository;
    @Mock
    private VoteRepository repository;
    @Mock
    private CpfValidatorClient client;

    @Test
    void shouldSaveVote() {
        final var request = Instancio.create(VoteRequestDTO.class);
        final var votingSessionEntity = Instancio.create(VotingSessionEntity.class);
        final var voteEntity = Instancio.create(VoteEntity.class);
        votingSessionEntity.setStatus(OPENED);

        when(votingSessionRepository.findById(request.votingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(empty());
        when(repository.save(any())).thenReturn(Mono.just(voteEntity));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.id().equals(voteEntity.getId()))
                .expectComplete()
                .verify();

        verify(repository).save(any());
    }

    @Test
    void shouldReturnExceptionOnSaveVote() {
        final var request = Instancio.create(VoteRequestDTO.class);
        final var votingSessionEntity = Instancio.create(VotingSessionEntity.class);
        votingSessionEntity.setStatus(OPENED);

        when(votingSessionRepository.findById(request.votingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(empty());
        when(repository.save(any())).thenReturn(Mono.error(() -> new DuplicateKeyException("")));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository).save(any());
    }

    @Test
    void shouldReturnExceptionOnValidateCpf() {
        final var request = Instancio.create(VoteRequestDTO.class);
        final var votingSessionEntity = Instancio.create(VotingSessionEntity.class);
        votingSessionEntity.setStatus(OPENED);

        when(votingSessionRepository.findById(request.votingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(Mono.error(new RuntimeException()));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnExceptionWhenClosed() {
        final var request = Instancio.create(VoteRequestDTO.class);
        final var votingSessionEntity = Instancio.create(VotingSessionEntity.class);
        votingSessionEntity.setStatus(CLOSED);

        when(votingSessionRepository.findById(request.votingSessionId())).thenReturn(Mono.just(votingSessionEntity));
        when(client.validateCpf(any())).thenReturn(empty());

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindAllVotes() {
        final var entity = Instancio.create(VoteEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = VoteDTO.builder()
                .id(entity.getId())
                .voteEnum(entity.getVoteEnum())
                .cpf(entity.getCpf())
                .votingSessionId(entity.getVotingSessionId())
                .build();

        verify(repository).findAll();

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindVoteById() {
        final var entity = Instancio.create(VoteEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getById(id);

        final var expected = VoteDTO.builder()
                .id(entity.getId())
                .voteEnum(entity.getVoteEnum())
                .votingSessionId(entity.getVotingSessionId())
                .cpf(entity.getCpf())
                .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

}