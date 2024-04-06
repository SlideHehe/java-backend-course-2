package edu.java.bot.domain.updates.kafka;

import edu.java.bot.KafkaIntegrationTest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.updates.UpdatesService;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class UpdateRequestsListenerTest extends KafkaIntegrationTest {
    @Autowired
    ApplicationConfig applicationConfig;
    @SpyBean
    @Autowired
    UpdateRequestsListener updateRequestsListener;
    @MockBean
    UpdateRequestDeadLetterQueueProducer deadLetterQueueProducer;
    @MockBean
    UpdatesService updatesService;

    @Test
    @DisplayName("Проверка корректной обработки сообщения")
    void listenCorrectMessage() {
        // given
        LinkUpdateRequest request = new LinkUpdateRequest(URI.create("https://aboba.com"), "hello", List.of());
        try (KafkaProducer<String, LinkUpdateRequest> kafkaProducer = new KafkaProducer<>(producerProps())) {
            // when
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));

            // then
            verify(updateRequestsListener, after(3000).times(3)).listen(eq(request), any());
            verify(updatesService, after(3000).times(3)).createUpdate(request);
        }
    }

    @Test
    @DisplayName("Проверка передачи некорректного сообщения в DLQ")
    void listenErrorMessage() {
        // given
        LinkUpdateRequest request = new LinkUpdateRequest(URI.create("https://aboba.com"), "hello", List.of());
        doThrow(new RuntimeException()).when(updatesService).createUpdate(any());
        try (KafkaProducer<String, LinkUpdateRequest> kafkaProducer = new KafkaProducer<>(producerProps())) {
            // when
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));
            kafkaProducer.send(new ProducerRecord<>(applicationConfig.kafka().updatesTopic().name(), request));

            // then
            verify(updateRequestsListener, after(3000).times(3)).listen(eq(request), any());
            verify(deadLetterQueueProducer, after(3000).times(3)).send(request);
        }
    }
}
