package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.scheduler.updatesender.HttpUpdateSender;
import edu.java.scrapper.scheduler.updatesender.ScrapperQueueProducer;
import edu.java.scrapper.scheduler.updatesender.UpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class UpdateSenderConfiguration {
    @Bean
    @ConditionalOnProperty(name = "app.use-queue", havingValue = "true")
    UpdateSender scrapperQueueProducer(
        KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        return new ScrapperQueueProducer(updateRequestKafkaTemplate, applicationConfig);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-queue", havingValue = "false")
    UpdateSender httpUpdateSender(BotClient botClient) {
        return new HttpUpdateSender(botClient);
    }
}
