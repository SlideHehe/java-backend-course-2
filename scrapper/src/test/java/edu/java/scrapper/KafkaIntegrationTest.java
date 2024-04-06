package edu.java.scrapper;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@DirtiesContext
@Testcontainers
@EmbeddedKafka(partitions = 1, ports = {39092}, kraft = false)
public abstract class KafkaIntegrationTest {
    /*
    Пришлось немного продублировать класс интеграционного теста с постгресом,
    т.к. чтобы предыдущие тесты работали адекватно, пришлось переопределять проперти use-queue на false.
    Однако если унаследоваться от IntegrationTest здесь и снова переопределить, но уже на true,
    то Spring не подгружает нужные бины и считает, что значение = false
     */
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();
    }

    protected Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:39092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1_000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class);
        return props;
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("app.use-queue", () -> true);
        registry.add("app.kafka.producer-properties.batch-size", () -> 0);
        registry.add("app.kafka.producer-properties.linger-ms", () -> 0);
        registry.add("app.kafka.producer-properties.acks-mode", () -> "0");
        registry.add("app.kafka.producer-properties.delivery-timeout", () -> Duration.ofSeconds(60));
        registry.add("app.kafka.producer-properties.bootstrap-servers", () -> "localhost:39092");
        registry.add("app.kafka.updates-topic.name", () -> "updates");
        registry.add("app.kafka.updates-topic.partitions", () -> 1);
        registry.add("app.kafka.updates-topic.replicas", () -> 1);
        registry.add("app.scheduler.enable", () -> false);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.enabled", () -> false);
    }
}
