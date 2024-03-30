package edu.java.bot.configuration.client;

import edu.java.bot.client.ClientFactory;
import edu.java.bot.client.scrapper.ScrapperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScrapperClientConfiguration {
    @Bean
    ScrapperClient getScrapperClient(ClientFactory clientFactory) {
        return clientFactory.createScrapperClient();
    }
}
