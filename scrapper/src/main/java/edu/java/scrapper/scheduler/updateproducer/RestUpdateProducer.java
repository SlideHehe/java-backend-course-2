package edu.java.scrapper.scheduler.updateproducer;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;

@Slf4j
@RequiredArgsConstructor
public class RestUpdateProducer implements UpdateProducer {
    private final BotClient botClient;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        try {
            botClient.createUpdate(linkUpdateRequest);
        } catch (WebClientException e) {
            log.error(e.getMessage());
        }
    }
}
