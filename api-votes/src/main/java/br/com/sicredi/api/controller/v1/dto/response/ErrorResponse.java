package br.com.sicredi.api.controller.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@With
public record ErrorResponse(String error,
                            String message,
                            @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date) {
}