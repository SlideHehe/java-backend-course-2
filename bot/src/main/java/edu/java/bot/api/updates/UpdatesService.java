package edu.java.bot.api.updates;

import edu.java.bot.api.updates.dto.LinkUpdateRequest;

public interface UpdatesService {
    void createUpdate(LinkUpdateRequest linkUpdateRequest);
}
