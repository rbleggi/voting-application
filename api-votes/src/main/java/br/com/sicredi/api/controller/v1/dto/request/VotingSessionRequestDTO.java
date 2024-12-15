package br.com.sicredi.api.controller.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.time.Duration;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@With
@Builder
public record VotingSessionRequestDTO(@NotNull UUID pollId,
                                      @Schema(example = "PT1M")
                                      @JsonFormat(shape = STRING) Duration duration) {
}