package edu.java.scrapper.api.links;

import edu.java.scrapper.api.links.dto.LinkResponse;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {
    private LinkMapper() {
    }

    public static LinkResponse linkToLinkResponse(Link link) {
        return new LinkResponse(link.id(), link.url());
    }
}
