package br.com.sicredi.api.domain.entity;

import br.com.sicredi.api.domain.enums.SessionStatusEnum;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.Duration.ofMinutes;
import static java.time.LocalDateTime.now;

@Data
@With
@Builder
@Table(name = "voting_session_entity")
public class VotingSessionEntity {

    @Id
    private UUID id;
    @Default
    private Duration duration = ofMinutes(1);
    private SessionStatusEnum status;
    @Default
    private LocalDateTime openedDate = now();
    private LocalDateTime closedDate;

}