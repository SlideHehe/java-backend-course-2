package edu.java.bot.domain.updates.kafka;

import edu.java.bot.domain.updates.UpdatesService;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateRequestsListener {
    private final UpdatesService updatesService;
    private final UpdateRequestDeadLetterQueueProducer deadLetterQueueProducer;

    @KafkaListener(topics = "${app.kafka.updates-topic.name}",
                   groupId = "${app.kafka.consumer-properties.group-id}",
                   containerFactory = "updateRequestContainerFactory")
    public void listen(@Payload LinkUpdateRequest updateRequest, Acknowledgment ack) {
        try {
            updatesService.createUpdate(updateRequest);
        } catch (Exception e) {
            deadLetterQueueProducer.send(updateRequest);
        }
        ack.acknowledge();
    }
}
