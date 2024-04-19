package edu.java.bot.domain.updates.kafka;

import edu.java.bot.KafkaIntegrationTest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateRequestDeadLetterQueueProducerTest extends KafkaIntegrationTest {
    @Autowired
    ApplicationConfig applicationConfig;
    @Autowired
    UpdateRequestDeadLetterQueueProducer deadLetterQueueProducer;

    @Test
    @DisplayName("Проверка отправки сообщений в топик updates_dlq")
    void sendUpdateRequest() {
        // given
        try (KafkaConsumer<String, LinkUpdateRequest> consumer = new KafkaConsumer<>(consumerProps())) {
            consumer.subscribe(List.of(applicationConfig.kafka().updatesDlqTopic().name()));
            deadLetterQueueProducer.send(new LinkUpdateRequest(URI.create("https://aboba.com"), "aaaa", List.of()));

            // when
            int recordsCount = consumer.poll(Duration.ofSeconds(1)).count();

            // then
            assertThat(recordsCount).isEqualTo(1);
        }
    }
}
