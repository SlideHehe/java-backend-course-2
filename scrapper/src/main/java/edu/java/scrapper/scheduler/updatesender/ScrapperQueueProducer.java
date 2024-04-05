package edu.java.scrapper.scheduler.updatesender;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate;
    private final ApplicationConfig applicationConfig;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        String topicName = applicationConfig.kafka().updatesTopic().name();
        updateRequestKafkaTemplate.send(topicName, linkUpdateRequest);
    }
}
