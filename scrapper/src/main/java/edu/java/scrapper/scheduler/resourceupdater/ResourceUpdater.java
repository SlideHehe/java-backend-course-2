package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.scheduler.UpdateInfo;
import java.util.Optional;

public interface ResourceUpdater {
    boolean supports(Link link);

    Optional<UpdateInfo> updateResource(Link link);
}
