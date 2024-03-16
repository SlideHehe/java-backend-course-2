package edu.java.bot.configuration;

import edu.java.bot.client.ClientFactory;
import edu.java.bot.client.scrapper.ScrapperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    ScrapperClient getScrapperClient(ApplicationConfig applicationConfig) {
        return ClientFactory.createScrapperClient(applicationConfig);
    }
}
