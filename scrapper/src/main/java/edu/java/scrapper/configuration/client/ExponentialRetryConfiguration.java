package edu.java.scrapper.configuration.client;

import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Configuration
public class ExponentialRetryConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "app",
                           name = "stackoverflow-client.retry.backoff-policy",
                           havingValue = "exponential")
    ExchangeFilterFunction stackoverflowRetryFilter(ApplicationConfig applicationConfig) {
        return getExponentialRetryFilter(applicationConfig.stackoverflowClient());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "github-client.retry.backoff-policy", havingValue = "exponential")
    ExchangeFilterFunction githubRetryFilter(ApplicationConfig applicationConfig) {
        return getExponentialRetryFilter(applicationConfig.stackoverflowClient());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "bot-client.retry.backoff-policy", havingValue = "exponential")
    ExchangeFilterFunction botRetryFilter(ApplicationConfig applicationConfig) {
        return getExponentialRetryFilter(applicationConfig.botClient());
    }

    private static ExchangeFilterFunction getExponentialRetryFilter(ApplicationConfig.Client client) {
        int maxAttempts = client.retry().maxAttempts();
        Duration initialBackoff = client.retry().initialBackoff();
        Set<Integer> statusCodes =
            Objects.requireNonNullElse(client.retry().retryableCodes(), Set.of());

        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> clientResponse.statusCode().isError())
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
            .retryWhen(Retry.backoff(maxAttempts, initialBackoff)
                .filter(throwable -> throwable instanceof WebClientResponseException webClientResponseException
                                     && statusCodes.contains(webClientResponseException.getStatusCode().value()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()));
    }
}
