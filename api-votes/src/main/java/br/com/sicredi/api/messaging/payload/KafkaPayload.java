package br.com.sicredi.api.messaging.payload;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record KafkaPayload<T>(T payload,
                              Metadata metadata) {
}