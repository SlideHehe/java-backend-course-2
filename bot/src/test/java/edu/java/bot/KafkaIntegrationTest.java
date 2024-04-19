package edu.java.bot;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.updates.UpdatesService;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import edu.java.bot.domain.updates.kafka.UpdateRequestDeadLetterQueueListener;
import edu.java.bot.domain.updates.kafka.UpdateRequestDeadLetterQueueProducer;
import edu.java.bot.domain.updates.kafka.UpdateRequestsListener;
import edu.java.bot.telegram.bot.LinkTrackerBot;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, ports = {39092}, kraft = false)
public abstract class KafkaIntegrationTest {
    @MockBean
    @Autowired
    LinkTrackerBot linkTrackerBot;

    protected Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:39092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-0");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1_000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class);
        return props;
    }

    protected Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:39092");
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60_000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.producer-properties.batch-size", () -> 0);
        registry.add("app.kafka.producer-properties.linger-ms", () -> 0);
        registry.add("app.kafka.producer-properties.acks-mode", () -> "0");
        registry.add("app.kafka.producer-properties.delivery-timeout", () -> Duration.ofSeconds(60));
        registry.add("app.kafka.updates-topic.name", () -> "updates");
        registry.add("app.kafka.updates-topic.partitions", () -> 1);
        registry.add("app.kafka.updates-topic.replicas", () -> 1);
        registry.add("app.kafka.consumer-properties.group-id", () -> "consumer-group");
        registry.add("app.kafka.consumer-properties.auto-offset-reset", () -> "earliest");
        registry.add("app.kafka.consumer-properties.max-poll-interval-ms", () -> 1_000);
        registry.add("app.kafka.consumer-properties.concurrency", () -> 1);
        registry.add("app.kafka.producer-properties.bootstrap-servers", () -> "localhost:39092");
        registry.add("app.kafka.consumer-properties.bootstrap-servers", () -> "localhost:39092");
    }
}
