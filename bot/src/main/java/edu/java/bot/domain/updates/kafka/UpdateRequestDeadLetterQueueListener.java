package edu.java.bot.domain.updates.kafka;

import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateRequestDeadLetterQueueListener {
    @KafkaListener(topics = "${app.kafka.updates-dlq-topic.name}",
                   groupId = "${app.kafka.consumer-properties.group-id}",
                   containerFactory = "updateRequestContainerFactory")
    public void listen(@Payload LinkUpdateRequest updateRequest, Acknowledgment ack) {
        log.info("Received link update request from dead letter queue: {}", updateRequest);
        ack.acknowledge();
    }
}
