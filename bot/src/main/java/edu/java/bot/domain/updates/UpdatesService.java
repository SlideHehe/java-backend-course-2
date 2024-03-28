package edu.java.bot.domain.updates;

import edu.java.bot.domain.updates.dto.LinkUpdateRequest;

public interface UpdatesService {
    void createUpdate(LinkUpdateRequest linkUpdateRequest);
}
