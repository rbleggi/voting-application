package br.com.sicredi.api.controller.v1.dto.response;

import br.com.sicredi.api.domain.enums.SessionStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@With
@Builder
public record VotingSessionDTO(UUID id,
                               String duration,
                               SessionStatusEnum status,
                               @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime openedDate,
                               @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime closedDate) {
}