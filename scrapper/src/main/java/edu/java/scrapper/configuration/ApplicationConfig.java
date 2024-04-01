package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean
    @NotNull
    Scheduler scheduler,
    @NotNull
    AccessType databaseAccessType,
    @NotNull
    Client githubClient,
    @NotNull
    Client stackoverflowClient,
    @NotNull
    Client botClient
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Client(String baseUrl, @NotNull Retry retry) {
        public record Retry(
            @NotNull
            Integer maxAttempts,
            @NotNull
            BackoffPolicy backoffPolicy,
            @NotNull
            Duration initialBackoff,
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
}
