package edu.java.scrapper.scheduler;

import edu.java.scrapper.domain.links.Link;

public record UpdateInfo(
    Link link,
    String description
) {
}
