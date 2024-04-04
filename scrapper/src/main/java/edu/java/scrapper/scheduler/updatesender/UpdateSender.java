package edu.java.scrapper.scheduler.updatesender;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;

public interface UpdateSender {
    void send(LinkUpdateRequest linkUpdateRequest);
}
