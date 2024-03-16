package edu.java.scrapper.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.scheduler.enable")
public class LinkUpdaterScheduler {
    LinkUpdater jdbcLinkUpdater;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        jdbcLinkUpdater.update();
    }
}
