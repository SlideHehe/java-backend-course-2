package edu.java.scrapper.configurations;

import edu.java.scrapper.clients.ClientFactory;
import edu.java.scrapper.clients.github.GithubClient;
import edu.java.scrapper.clients.stackoverflow.StackoverflowClient;
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
}
