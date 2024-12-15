package br.com.sicredi.api.messaging;

import br.com.sicredi.api.messaging.messages.VoteResultMessage;
import br.com.sicredi.api.messaging.payload.KafkaPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class VoteResultProducer extends KafkaProducer<VoteResultMessage> {

    @Value("${kafka.provider}")
    private String provider;

    @Value("${kafka.user-created.topic}")
    private String topic;

    @Value("${kafka.user-created.event-type}")
    private String eventType;

    @Value("${kafka.user-created.payload-version}")
    private String version;

    public VoteResultProducer(final KafkaTemplate<String, KafkaPayload<VoteResultMessage>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final VoteResultMessage message) {
        send(message, eventType, provider, version, topic);
    }

}