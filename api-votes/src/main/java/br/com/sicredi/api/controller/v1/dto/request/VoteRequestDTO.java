package br.com.sicredi.api.controller.v1.dto.request;

import br.com.sicredi.api.domain.enums.VoteEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder
public record VoteRequestDTO(@NotNull UUID votingSessionId,
                             @NotNull String cpf,
                             @NotNull VoteEnum vote) {
}