package br.com.sicredi.api.messaging.payload;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record Metadata(String eventType,
                       String provider,
                       String version) {
}