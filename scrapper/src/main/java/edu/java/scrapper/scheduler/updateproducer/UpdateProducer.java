package edu.java.scrapper.scheduler.updateproducer;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;

public interface UpdateProducer {
    void send(LinkUpdateRequest linkUpdateRequest);
}
