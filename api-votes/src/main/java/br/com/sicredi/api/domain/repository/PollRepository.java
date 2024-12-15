package br.com.sicredi.api.domain.repository;

import br.com.sicredi.api.domain.entity.PollEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends ReactiveCrudRepository<PollEntity, UUID> {
}