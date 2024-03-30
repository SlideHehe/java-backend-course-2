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
    @NotEmpty
    String telegramToken,
    @NotNull
    Client scrapperClient
) {
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
}
