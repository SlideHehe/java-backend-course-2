package edu.java.bot.client;

import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.configuration.ApplicationConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ClientFactory {
    private static final String SCRAPPER_BASE_URL = "http://localhost:8080";

    private ClientFactory() {
    }

    public static ScrapperClient createScrapperClient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = SCRAPPER_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().scrapperApiUrl() != null) {
            baseUrl = applicationConfig.client().scrapperApiUrl();
        }
        return getFactory(baseUrl).createClient(ScrapperClient.class);
    }

    private static HttpServiceProxyFactory getFactory(String baseUrl) {
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }
}
