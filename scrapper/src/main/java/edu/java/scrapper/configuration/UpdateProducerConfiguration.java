package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.scheduler.updateproducer.RestUpdateProducer;
import edu.java.scrapper.scheduler.updateproducer.ScrapperQueueProducer;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class UpdateProducerConfiguration {
    @Bean
    @ConditionalOnProperty(name = "app.use-queue", havingValue = "true")
    UpdateProducer scrapperQueueProducer(
        KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        return new ScrapperQueueProducer(updateRequestKafkaTemplate, applicationConfig);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-queue", havingValue = "false")
    UpdateProducer httpUpdateSender(BotClient botClient) {
        return new RestUpdateProducer(botClient);
    }
}
