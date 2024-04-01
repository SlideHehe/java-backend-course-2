package edu.java.scrapper.client;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.configuration.ApplicationConfig;
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
    private static final String STACKOVERFLOW_BASE_URL = "https://api.stackexchange.com/2.3";
    private static final String GITHUB_BASE_URL = "https://api.github.com";
    private static final String BOT_BASE_URL = "http://localhost:8090";
    private final ApplicationConfig applicationConfig;
    private final ExchangeFilterFunction stackoverflowRetryFilter;
    private final ExchangeFilterFunction githubRetryFilter;
    private final ExchangeFilterFunction botRetryFilter;

    public StackoverflowClient createStackoverflowClient() {
        String baseUrl =
            Objects.requireNonNullElse(applicationConfig.stackoverflowClient().baseUrl(), STACKOVERFLOW_BASE_URL);
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(stackoverflowRetryFilter)
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(StackoverflowClient.class);
    }

    public GithubClient createGithubclient() {
        String baseUrl = Objects.requireNonNullElse(applicationConfig.githubClient().baseUrl(), GITHUB_BASE_URL);
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(githubRetryFilter)
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GithubClient.class);
    }

    public BotClient createBotClient() {
        String baseUrl = Objects.requireNonNullElse(applicationConfig.botClient().baseUrl(), BOT_BASE_URL);
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(botRetryFilter)
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(BotClient.class);
    }
}
