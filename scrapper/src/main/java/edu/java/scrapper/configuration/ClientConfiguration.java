package edu.java.scrapper.configuration;

import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    StackoverflowClient getStackoverflowClient(ApplicationConfig applicationConfig) {
        return ClientFactory.createStackoverflowClient(applicationConfig);
    }

    @Bean
    GithubClient getGithubClient(ApplicationConfig applicationConfig) {
        return ClientFactory.createGithubclient(applicationConfig);
    }

    @Bean
    BotClient getBotClient(ApplicationConfig applicationConfig) {
        return ClientFactory.createBotClient(applicationConfig);
    }
}
