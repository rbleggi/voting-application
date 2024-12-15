package br.com.sicredi.api.controller.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PollRequestDTO(@NotNull String name,
                             @NotNull String description) {
}