package br.com.sicredi.api.service;

import br.com.sicredi.api.controller.v1.dto.request.PollRequestDTO;
import br.com.sicredi.api.controller.v1.dto.response.PollDTO;
import br.com.sicredi.api.domain.entity.PollEntity;
import br.com.sicredi.api.domain.repository.PollRepository;
import br.com.sicredi.api.service.PollService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @InjectMocks
    private PollService service;

    @Mock
    private PollRepository repository;

    @Test
    void shouldSavePoll() {
        final var request = Instancio.create(PollRequestDTO.class);
        final var entity = Instancio.create(PollEntity.class);

        when(repository.save(any())).thenReturn(Mono.just(entity));

        final var result = service.save(request);

        verify(repository).save(any());

        StepVerifier.create(result)
                .expectNextMatches(response -> response.id().equals(entity.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    void shouldReturnExceptionOnSavePoll() {
        final var request = Instancio.create(PollRequestDTO.class);

        when(repository.save(any())).thenReturn(Mono.error(RuntimeException::new));

        final var result = service.save(request);

        StepVerifier.create(result)
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldFindAllPolls() {
        final var entity = Instancio.create(PollEntity.class);

        when(repository.findAll()).thenReturn(Flux.just(entity));

        final var result = service.findAll();

        final var expected = PollDTO.builder()
                .id(entity.getId())
                .votesInFavour(entity.getVotesInFavour())
                .votesAgainst(entity.getVotesAgainst())
                .totalVotes(entity.getTotalVotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .description(entity.getDescription())
                .name(entity.getName())
                .build();

        verify(repository).findAll();

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindPollById() {
        final var entity = Instancio.create(PollEntity.class);

        final var id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.just(entity));

        final var result = service.getPollDTO(id);

        final var expected = PollDTO.builder()
                .id(entity.getId())
                .votesInFavour(entity.getVotesInFavour())
                .votesAgainst(entity.getVotesAgainst())
                .totalVotes(entity.getTotalVotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .description(entity.getDescription())
                .name(entity.getName())
                .build();

        verify(repository).findById(id);

        StepVerifier.create(result)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

}