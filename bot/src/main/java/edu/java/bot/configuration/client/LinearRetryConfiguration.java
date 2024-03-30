package edu.java.bot.configuration.client;

import edu.java.bot.client.exchangefilterfunction.LinearRetryFilter;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Configuration
public class LinearRetryConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "app", name = "scrapper-client.retry.backoff-policy", havingValue = "linear")
    ExchangeFilterFunction scrapperRetryFilter(ApplicationConfig applicationConfig) {
        return new LinearRetryFilter(applicationConfig.scrapperClient());
    }
}
