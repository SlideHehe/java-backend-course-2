package edu.java.scrapper.clients;

import edu.java.scrapper.clients.github.GithubClient;
import edu.java.scrapper.clients.stackoverflow.StackoverflowClient;
import edu.java.scrapper.configurations.ApplicationConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ClientFactory {
    private static final String STACKOVERFLOW_BASE_URL = "https://api.stackexchange.com/2.3";
    private static final String GITHUB_BASE_URL = "https://api.github.com";

    private ClientFactory() {
    }

    public static StackoverflowClient createStackoverflowClient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = STACKOVERFLOW_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().stackoverflowApiUrl() != null) {
            baseUrl = applicationConfig.client().stackoverflowApiUrl();
        }

        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(StackoverflowClient.class);
    }

    public static GithubClient createGithubclient(@NotNull ApplicationConfig applicationConfig) {
        String baseUrl = GITHUB_BASE_URL;

        if (applicationConfig.client() != null && applicationConfig.client().githubApiUrl() != null) {
            baseUrl = applicationConfig.client().githubApiUrl();
        }

        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(GithubClient.class);
    }
}
