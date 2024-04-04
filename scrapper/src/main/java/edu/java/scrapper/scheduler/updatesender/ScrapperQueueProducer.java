package edu.java.scrapper.scheduler.updatesender;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.use-queue", havingValue = "true")
public class ScrapperQueueProducer implements UpdateSender {
    private final KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate;
    private final ApplicationConfig applicationConfig;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        String topicName = applicationConfig.kafka().updatesTopic().name();
        updateRequestKafkaTemplate.send(topicName, linkUpdateRequest);
    }
}
