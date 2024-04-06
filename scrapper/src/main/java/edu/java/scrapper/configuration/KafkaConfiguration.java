package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@ConditionalOnProperty(name = "app.use-queue")
public class KafkaConfiguration {
    @Bean
    public KafkaAdmin kafkaAdmin(ApplicationConfig applicationConfig) {
        var producerProps = applicationConfig.kafka().producerProperties();
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, producerProps.bootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic updatesTopic(ApplicationConfig applicationConfig) {
        var updatesTopic = applicationConfig.kafka().updatesTopic();
        return TopicBuilder.name(updatesTopic.name())
            .partitions(updatesTopic.partitions())
            .replicas(updatesTopic.replicas())
            .build();
    }

    @Bean
    public ProducerFactory<String, LinkUpdateRequest> producerFactory(ApplicationConfig applicationConfig) {
        var producerProperties = applicationConfig.kafka().producerProperties();
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.bootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, producerProperties.acksMode());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) producerProperties.deliveryTimeout().toMillis());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producerProperties.lingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperties.batchSize());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate(
        ProducerFactory<String, LinkUpdateRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
