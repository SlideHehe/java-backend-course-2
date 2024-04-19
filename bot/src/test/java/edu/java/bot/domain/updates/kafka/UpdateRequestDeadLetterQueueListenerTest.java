package edu.java.bot.domain.updates.kafka;

import edu.java.bot.KafkaIntegrationTest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.verify;

class UpdateRequestDeadLetterQueueListenerTest extends KafkaIntegrationTest {
    @Autowired
    ApplicationConfig applicationConfig;
    @SpyBean
    @Autowired
    UpdateRequestDeadLetterQueueListener deadLetterQueueListener;

    @Test
    @DisplayName("Проверка принятия сообщений из dlq топика")
    void messagesFromDlqReceived() {
        // given
        String dlqTopicName = applicationConfig.kafka().updatesDlqTopic().name();
        LinkUpdateRequest request = new LinkUpdateRequest(URI.create("https://aboba.com"), "hello", List.of());
        try (KafkaProducer<String, LinkUpdateRequest> kafkaProducer = new KafkaProducer<>(producerProps())) {
            // when
            kafkaProducer.send(new ProducerRecord<>(dlqTopicName, request));
            kafkaProducer.send(new ProducerRecord<>(dlqTopicName, request));
            kafkaProducer.send(new ProducerRecord<>(dlqTopicName, request));

            // then
            verify(deadLetterQueueListener, after(1000).times(3)).listen(eq(request), any());
        }
    }
}
