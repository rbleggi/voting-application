package br.com.sicredi.api.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteEnum {

    SIM(true),
    NAO(false);

    private final Boolean value;

}