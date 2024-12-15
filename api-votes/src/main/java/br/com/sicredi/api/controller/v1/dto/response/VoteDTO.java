package br.com.sicredi.api.controller.v1.dto.response;

import br.com.sicredi.api.domain.enums.VoteEnum;
import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder
public record VoteDTO(UUID id,
                      UUID votingSessionId,
                      String cpf,
                      VoteEnum voteEnum) {
}