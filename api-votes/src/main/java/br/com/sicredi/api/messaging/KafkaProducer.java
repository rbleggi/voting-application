package br.com.sicredi.api.messaging;

import br.com.sicredi.api.messaging.payload.KafkaPayload;
import br.com.sicredi.api.messaging.payload.Metadata;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class KafkaProducer<T> {

    protected KafkaTemplate<String, KafkaPayload<T>> kafkaTemplate;

    public void send(final T message, String eventType, String provider, String version, String topic) {
        kafkaTemplate.send(topic, new KafkaPayload<>(message, Metadata.builder()
                .eventType(eventType)
                .provider(provider)
                .version(version != null ? version : "1.0")
                .build()));
    }

}