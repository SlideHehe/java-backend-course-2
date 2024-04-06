package edu.java.bot.configuration;

import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic updatesTopic(ApplicationConfig applicationConfig) {
        var updatesTopic = applicationConfig.kafka().updatesTopic();
        return TopicBuilder.name(updatesTopic.name())
            .partitions(updatesTopic.partitions())
            .replicas(updatesTopic.replicas())
            .build();
    }

    @Bean
    public NewTopic updatesDlqTopic(ApplicationConfig applicationConfig) {
        var dlqTopic = applicationConfig.kafka().updatesDlqTopic();
        return TopicBuilder.name(dlqTopic.name())
            .partitions(dlqTopic.partitions())
            .replicas(dlqTopic.replicas())
            .build();
    }

    @Bean
    public ConsumerFactory<String, LinkUpdateRequest> updateRequestConsumerFactory(
        ApplicationConfig applicationConfig
    ) {
        var consumerProperties = applicationConfig.kafka().consumerProperties();
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProperties.autoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerProperties.maxPollIntervalMs());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> updateRequestContainerFactory(
        ConsumerFactory<String, LinkUpdateRequest> consumerFactory, ApplicationConfig applicationConfig
    ) {
        var consumerProperties = applicationConfig.kafka().consumerProperties();
        var factory = new ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(consumerProperties.concurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
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
