package edu.java.bot.domain.updates;

import edu.java.bot.domain.updates.dto.LinkUpdate;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;

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
