package br.com.sicredi.api.messaging.messages;

import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder
public record VoteResultMessage(UUID votingSessionId,
                                Integer totalVotes,
                                Integer votesAgainst,
                                Integer votesInFavour) {
}