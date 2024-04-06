package edu.java.scrapper.scheduler.updateproducer;

import edu.java.scrapper.KafkaIntegrationTest;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScrapperQueueProducerTest extends KafkaIntegrationTest {
    @Autowired
    ApplicationConfig applicationConfig;
    @Autowired
    UpdateProducer scrapperQueueProducer;

    @Test
    @DisplayName("Проверка отправки сообщений в топик updates")
    void sendUpdateRequest() {
        // given
        try (KafkaConsumer<String, LinkUpdateRequest> consumer = new KafkaConsumer<>(consumerProps())) {
            consumer.subscribe(List.of(applicationConfig.kafka().updatesTopic().name()));
            scrapperQueueProducer.send(new LinkUpdateRequest(URI.create("https://aboba.com"), "aaaa", List.of()));

            // when
            int recordsCount = consumer.poll(Duration.ofSeconds(1)).count();

            // then
            assertThat(recordsCount).isEqualTo(1);
        }
    }
}
