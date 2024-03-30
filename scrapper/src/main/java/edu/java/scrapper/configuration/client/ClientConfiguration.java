package edu.java.scrapper.configuration.client;

import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    StackoverflowClient getStackoverflowClient(ClientFactory clientFactory) {
        return clientFactory.createStackoverflowClient();
    }

    @Bean
    GithubClient getGithubClient(ClientFactory clientFactory) {
        return clientFactory.createGithubclient();
    }

    @Bean
    BotClient getBotClient(ClientFactory clientFactory) {
        return clientFactory.createBotClient();
    }
}
