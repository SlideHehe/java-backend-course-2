package edu.java.scrapper.scheduler;

import edu.java.scrapper.domain.links.schemabased.Link;

public record UpdateInfo(
    Link link,
    String description
) {
}
