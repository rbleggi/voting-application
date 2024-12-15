package br.com.sicredi.api.domain.repository;

import br.com.sicredi.api.domain.entity.VoteEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface VoteRepository extends ReactiveCrudRepository<VoteEntity, UUID> {
    Flux<VoteEntity> findAllByVotingSessionId(final UUID votingSessionId);
}