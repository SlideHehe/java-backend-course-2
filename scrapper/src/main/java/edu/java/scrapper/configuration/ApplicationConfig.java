package edu.java.scrapper.configuration;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean @NotNull Scheduler scheduler,
    @NotNull AccessType databaseAccessType,
    @NotNull Client githubClient,
    @NotNull Client stackoverflowClient,
    @NotNull Client botClient,
    @NotNull Boolean useQueue,
    @ConditionalOnProperty
    Kafka kafka
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

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

    public enum AccessType {
        JDBC, JOOQ, JPA
    }

    public record Kafka(@NotNull KafkaProducerProperties producerProperties, @NotNull Topic updatesTopic) {
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

    @AssertFalse
    public boolean isKafkaRequired() {
        if (useQueue) {
            return kafka == null;
        }
        return false;
    }
}
