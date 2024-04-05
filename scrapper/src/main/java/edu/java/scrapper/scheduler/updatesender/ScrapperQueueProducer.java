package edu.java.scrapper.scheduler.updatesender;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import org.springframework.kafka.core.KafkaTemplate;

public class ScrapperQueueProducer implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate;
    private final String updatesTopicName;

    public ScrapperQueueProducer(
        KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        this.updateRequestKafkaTemplate = updateRequestKafkaTemplate;
        this.updatesTopicName = applicationConfig.kafka().updatesTopic().name();
    }

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        updateRequestKafkaTemplate.send(updatesTopicName, linkUpdateRequest);
    }
}
