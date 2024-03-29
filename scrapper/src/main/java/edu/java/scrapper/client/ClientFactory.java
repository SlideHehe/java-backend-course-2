package edu.java.scrapper.client;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ClientFactory {
    private static final String STACKOVERFLOW_BASE_URL = "https://api.stackexchange.com/2.3";
    private static final String GITHUB_BASE_URL = "https://api.github.com";
    private static final String BOT_BASE_URL = "http://localhost:8090";

    private ClientFactory() {
    }

    public static StackoverflowClient createStackoverflowClient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = STACKOVERFLOW_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().stackoverflowApiUrl() != null) {
            baseUrl = applicationConfig.client().stackoverflowApiUrl();
        }
        return getFactory(baseUrl).createClient(StackoverflowClient.class);
    }

    public static GithubClient createGithubclient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = GITHUB_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().githubApiUrl() != null) {
            baseUrl = applicationConfig.client().githubApiUrl();
        }
        return getFactory(baseUrl).createClient(GithubClient.class);
    }

    public static BotClient createBotClient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = BOT_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().botApiUrl() != null) {
            baseUrl = applicationConfig.client().botApiUrl();
        }
        return getFactory(baseUrl).createClient(BotClient.class);
    }

    private static HttpServiceProxyFactory getFactory(String baseUrl) {
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }
}
