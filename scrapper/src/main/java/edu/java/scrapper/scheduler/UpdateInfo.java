package edu.java.scrapper.scheduler;

import edu.java.scrapper.api.links.Link;

public record UpdateInfo(
    Link link,
    String description
) {
}
