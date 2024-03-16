package edu.java.scrapper.client.bot;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {
    @PostExchange("/updates")
    void createUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest);
}
