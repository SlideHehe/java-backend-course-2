package edu.java.scrapper.client.exchangefilterfunction;

import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class LinearRetryFilter implements ExchangeFilterFunction {
    private final int maxAttempts;
    private final Duration initialBackoff;
    private final Set<Integer> retryableCodes;

    public LinearRetryFilter(ApplicationConfig.Client client) {
        ApplicationConfig.Client.Retry retry = client.retry();
        this.maxAttempts = retry.maxAttempts();
        this.initialBackoff = retry.initialBackoff();
        this.retryableCodes = Objects.requireNonNullElse(retry.retryableCodes(), Set.of());
    }

    @Override
    public @NotNull Mono<ClientResponse> filter(@NotNull ClientRequest request, @NotNull ExchangeFunction next) {
        return doRequest(request, next, 1);
    }

    private Mono<ClientResponse> doRequest(ClientRequest request, ExchangeFunction next, int retryCount) {
        return next.exchange(request)
            .flatMap(response -> {
                if (retryableCodes.contains(response.statusCode().value())
                    && retryCount <= maxAttempts) {
                    Duration delay = initialBackoff.multipliedBy(retryCount);
                    return Mono.delay(delay)
                        .then(Mono.defer(() -> doRequest(request, next, retryCount + 1)));
                } else {
                    return Mono.just(response);
                }
            });
    }
}
