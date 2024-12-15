package br.com.sicredi.api.messaging;

import br.com.sicredi.api.messaging.VoteResultProducer;
import br.com.sicredi.api.messaging.messages.VoteResultMessage;
import br.com.sicredi.api.messaging.payload.KafkaPayload;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @InjectMocks
    private VoteResultProducer producer;

    @Mock
    private KafkaTemplate<String, KafkaPayload<VoteResultMessage>> kafkaTemplate;


    @Test
    void shouldSendMessage() {
        producer.send(Instancio.create(VoteResultMessage.class));
        verify(kafkaTemplate).send(any(), any());
    }

}