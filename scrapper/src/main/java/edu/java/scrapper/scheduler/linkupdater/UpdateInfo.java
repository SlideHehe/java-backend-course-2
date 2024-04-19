package edu.java.scrapper.scheduler.linkupdater;

import edu.java.scrapper.domain.links.schemabased.Link;

public record UpdateInfo(
    Link link,
    String description
) {
}
