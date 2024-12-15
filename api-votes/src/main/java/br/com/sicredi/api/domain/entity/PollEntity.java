package br.com.sicredi.api.domain.entity;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Data
@With
@Builder
@Table(name = "poll_entity")
public class PollEntity {

    @Id
    private UUID id;
    private UUID votingSessionId;
    private String name;
    private String description;
    @Default
    private LocalDateTime createdAt = now();
    private LocalDateTime updatedAt;
    private Integer totalVotes;
    private Integer votesAgainst;
    private Integer votesInFavour;

}