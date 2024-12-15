package br.com.sicredi.api.service;

import br.com.sicredi.api.controller.v1.dto.request.VotingSessionRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.VotingSessionDTO;
import br.com.sicredi.api.domain.entity.PollEntity;
import br.com.sicredi.api.domain.entity.VoteEntity;
import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import br.com.sicredi.api.domain.repository.VoteRepository;
import br.com.sicredi.api.domain.repository.VotingSessionRepository;
import br.com.sicredi.api.messaging.VoteResultProducer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @InjectMocks
    private VotingSessionService service;

    @Mock
    private PollService pollService;
    @Mock
    private VotingSessionRepository repository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private VoteResultProducer voteResultProducer;

    @Test
    void shouldSaveVotingSession() throws InterruptedException {
        final var request = Instancio.create(VotingSessionRequestDTO.class).withDuration(Duration.ofSeconds(0));
        final var votingSessionEntity = Instancio.create(VotingSessionEntity.class);
        final var pollEntity = Instancio.create(PollEntity.class);
        final var voteEntity = Instancio.create(VoteEntity.class);
        pollEntity.setVotingSessionId(null);

        when(pollService.getPollEntity(any(UUID.class))).thenReturn(Mono.just(pollEntity));
        when(repository.save(any())).thenReturn(Mono.just(votingSessionEntity));
        when(pollService.save(any(PollEntity.class))).thenReturn(Mono.just(pollEntity));
        when(voteRepository.findAllByVotingSessionId(any())).thenReturn(Flux.just(voteEntity));

        final var result = service.save(request);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));

        TimeUnit.SECONDS.sleep(1);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.id().equals(votingSessionEntity.getId()))
                .expectComplete()
                .verify();

        verify(pollService, times(2)).getPollEntity(any(UUID.class));
        verify(repository, times(2)).save(any());
        verify(pollService, times(2)).save(any(PollEntity.class));
        verify(taskScheduler, times(1)).schedule(any(Runnable.class), any(Instant.class));
        verify(voteRepository, times(1)).findAllByVotingSessionId(any(UUID.class));
        verify(voteResultProducer, times(1)).send(any());
    }

    @Test
    void shouldReturnExceptionWhenExistsVotingSessionId() {
        final var request = Instancio.create(VotingSessionRequestDTO.class).withDuration(Duration.ofSeconds(0));
        final var pollEntity = Instancio.create(PollEntity.class);

        when(pollService.getPollEntity(any(UUID.class))).thenReturn(Mono.just(pollEntity));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(pollService, times(1)).getPollEntity(any(UUID.class));
    }

    @Test
    void shouldReturnExceptionWhenSaveVotingSession() {
        final var request = Instancio.create(VotingSessionRequestDTO.class).withDuration(Duration.ofSeconds(0));
        final var pollEntity = Instancio.create(PollEntity.class);
        pollEntity.setVotingSessionId(null);

        when(pollService.getPollEntity(any(UUID.class))).thenReturn(Mono.just(pollEntity));
        when(repository.save(any())).thenReturn(Mono.error(RuntimeException::new));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(pollService, times(1)).getPollEntity(any(UUID.class));
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldFindAllVotingSessions() {
        final var entity = Instancio.create(VotingSessionEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = VotingSessionDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .closedDate(entity.getClosedDate())
                .openedDate(entity.getOpenedDate())
                .duration(DurationFormatUtils.formatDuration(entity.getDuration().toMillis(), "HH:mm:ss", true))
                .build();

        verify(repository).findAll();

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindVotingSessionById() {
        final var entity = Instancio.create(VotingSessionEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getVotingSession(id);

        final var expected = VotingSessionDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .closedDate(entity.getClosedDate())
                .openedDate(entity.getOpenedDate())
                .duration(DurationFormatUtils.formatDuration(entity.getDuration().toMillis(), "HH:mm:ss", true))
                .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

}