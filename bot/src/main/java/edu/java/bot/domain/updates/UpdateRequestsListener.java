package edu.java.bot.domain.updates;

import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateRequestsListener {
    private final UpdatesService updatesService;

    @KafkaListener(topics = "${app.kafka.updates-topic.name}",
                   groupId = "${app.kafka.consumer-properties.group-id}",
                   containerFactory = "updateRequestContainerFactory")
    public void listen(@Payload LinkUpdateRequest updateRequest, Acknowledgment ack) {
        updatesService.createUpdate(updateRequest);
        log.info(updateRequest.toString());
        ack.acknowledge();
    }
}
