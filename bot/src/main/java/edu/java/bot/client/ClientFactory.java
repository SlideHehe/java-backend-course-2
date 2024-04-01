package edu.java.bot.client;

import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.configuration.ApplicationConfig;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
@RequiredArgsConstructor
public class ClientFactory {
    private static final String SCRAPPER_BASE_URL = "http://localhost:8080";
    private final ApplicationConfig applicationConfig;
    private final ExchangeFilterFunction scrapperRetryFilter;

    public ScrapperClient createScrapperClient() {
        String baseUrl = Objects.requireNonNullElse(applicationConfig.scrapperClient().baseUrl(), SCRAPPER_BASE_URL);
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(scrapperRetryFilter)
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ScrapperClient.class);
    }
}
