package br.com.sicredi.api.controller.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@With
@Builder
public record PollDTO(UUID id,
                      String name,
                      String description,
                      @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
                      @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt,
                      Integer totalVotes,
                      Integer votesAgainst,
                      Integer votesInFavour) {
}