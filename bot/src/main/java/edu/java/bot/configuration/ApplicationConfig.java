package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty String telegramToken,
    @NotNull Client scrapperClient,
    @NotNull Kafka kafka
) {
    public record Client(String baseUrl, @NotNull Retry retry) {
        public record Retry(
            @NotNull Integer maxAttempts,
            @NotNull BackoffPolicy backoffPolicy,
            @NotNull Duration initialBackoff,
            Set<Integer> retryableCodes
        ) {
            public enum BackoffPolicy {
                FIXED, LINEAR, EXPONENTIAL
            }
        }
    }

    public record Kafka(
        @NotNull KafkaConsumerProperties consumerProperties,
        @NotNull KafkaProducerProperties producerProperties,
        @NotNull Topic updatesTopic,
        @NotNull Topic updatesDlqTopic
    ) {
        public record KafkaConsumerProperties(
            @NotNull String bootstrapServers,
            @NotNull String groupId,
            @NotNull String autoOffsetReset,
            @NotNull Integer maxPollIntervalMs,
            @NotNull Integer concurrency
        ) {
        }

        public record KafkaProducerProperties(
            @NotNull String bootstrapServers,
            @NotNull String acksMode,
            @NotNull Duration deliveryTimeout,
            @NotNull Integer lingerMs,
            @NotNull Integer batchSize
        ) {
        }

        public record Topic(
            @NotNull String name,
            @NotNull Integer partitions,
            @NotNull Integer replicas
        ) {
        }
    }
}
