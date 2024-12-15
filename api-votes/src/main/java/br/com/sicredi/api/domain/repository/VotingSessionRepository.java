package br.com.sicredi.api.domain.repository;

import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotingSessionRepository extends ReactiveCrudRepository<VotingSessionEntity, UUID> {
}