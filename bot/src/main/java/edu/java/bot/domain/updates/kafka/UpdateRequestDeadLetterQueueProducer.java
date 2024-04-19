package edu.java.bot.domain.updates.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UpdateRequestDeadLetterQueueProducer {
    private final KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate;
    private final String updatesTopicName;

    public UpdateRequestDeadLetterQueueProducer(
        KafkaTemplate<String, LinkUpdateRequest> updateRequestKafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        this.updateRequestKafkaTemplate = updateRequestKafkaTemplate;
        this.updatesTopicName = applicationConfig.kafka().updatesDlqTopic().name();
    }

    public void send(LinkUpdateRequest updateRequest) {
        updateRequestKafkaTemplate.send(updatesTopicName, updateRequest);
    }
}
