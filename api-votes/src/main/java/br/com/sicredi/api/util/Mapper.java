package br.com.sicredi.api.util;

import br.com.sicredi.api.controller.v1.dto.response.ErrorResponse;
import br.com.sicredi.api.controller.v1.dto.response.PollDTO;
import br.com.sicredi.api.controller.v1.dto.response.VoteDTO;
import br.com.sicredi.api.controller.v1.dto.response.VotingSessionDTO;
import br.com.sicredi.api.domain.entity.PollEntity;
import br.com.sicredi.api.domain.entity.VoteEntity;
import br.com.sicredi.api.domain.entity.VotingSessionEntity;
import lombok.experimental.UtilityClass;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@UtilityClass
public class Mapper {

    public static PollDTO toPollDTO(PollEntity pollEntity) {
        return PollDTO.builder()
                .id(pollEntity.getId())
                .name(pollEntity.getName())
                .description(pollEntity.getDescription())
                .createdAt(pollEntity.getCreatedAt())
                .updatedAt(pollEntity.getUpdatedAt())
                .totalVotes(pollEntity.getTotalVotes())
                .votesAgainst(pollEntity.getVotesAgainst())
                .votesInFavour(pollEntity.getVotesInFavour())
                .build();
    }

    public static VotingSessionDTO toVotingSessionDTO(VotingSessionEntity votingSessionEntity) {
        return VotingSessionDTO.builder()
                .id(votingSessionEntity.getId())
                .openedDate(votingSessionEntity.getOpenedDate())
                .closedDate(votingSessionEntity.getClosedDate())
                .duration(formatDuration(votingSessionEntity.getDuration().toMillis(), "HH:mm:ss", true))
                .status(votingSessionEntity.getStatus())
                .build();
    }

    public static VoteDTO toVoteDTO(VoteEntity voteEntity) {
        return VoteDTO.builder()
                .id(voteEntity.getId())
                .votingSessionId(voteEntity.getVotingSessionId())
                .cpf(voteEntity.getCpf())
                .voteEnum(voteEntity.getVoteEnum())
                .build();
    }

    public static ErrorResponse toErrorResponse(String concatenatedErrorMessage, String error) {
        return ErrorResponse.builder()
                .message(concatenatedErrorMessage)
                .date(now())
                .build();
    }

}