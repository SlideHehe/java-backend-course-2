package edu.java.bot.api.updates;

import edu.java.bot.api.updates.dto.LinkUpdate;
import edu.java.bot.api.updates.dto.LinkUpdateRequest;

public class LinkUpdateMapper {
    private LinkUpdateMapper() {
    }

    public static LinkUpdate linkUpdateRequestToLinkUpdate(LinkUpdateRequest linkUpdateRequest) {
        return new LinkUpdate(
            linkUpdateRequest.url(),
            linkUpdateRequest.description()
        );
    }
}
