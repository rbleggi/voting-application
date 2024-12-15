package br.com.sicredi.api.domain.entity;

import br.com.sicredi.api.domain.enums.VoteEnum;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@With
@Builder
@Table(name = "vote_entity")
public class VoteEntity {

    @Id
    private UUID id;
    private UUID votingSessionId;
    private String cpf;
    private VoteEnum voteEnum;

}