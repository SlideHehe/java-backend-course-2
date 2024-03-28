package edu.java.scrapper.domain.links.schemabased;

import edu.java.scrapper.domain.links.dto.LinkResponse;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {
    private LinkMapper() {
    }

    public static LinkResponse linkToLinkResponse(Link link) {
        return new LinkResponse(link.id(), link.url());
    }
}
