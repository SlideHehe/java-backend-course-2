package edu.java.scrapper.domain.links;

import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.schemabased.Link;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {
    private LinkMapper() {
    }

    public static LinkResponse linkSchemaToLinkResponse(Link link) {
        return new LinkResponse(link.id(), link.url());
    }

    public static LinkResponse linkEntityToLinkResponse(edu.java.scrapper.domain.links.jpa.Link link) {
        return new LinkResponse(link.getId(), link.getUrl());
    }

    public static Link linkEntityToLinkSchema(edu.java.scrapper.domain.links.jpa.Link link) {
        return new Link(
            link.getId(),
            link.getUrl(),
            link.getUpdatedAt(),
            link.getCheckedAt(),
            link.getType(),
            link.getAnswerCount(),
            link.getCommentCount(),
            link.getPullRequestCount(),
            link.getCommitCount()
        );
    }
}
