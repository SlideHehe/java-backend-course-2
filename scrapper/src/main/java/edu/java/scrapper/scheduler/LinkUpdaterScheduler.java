package edu.java.scrapper.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduler.enable")
public class LinkUpdaterScheduler {
    private final LinkUpdater jdbcSchemaBasedLinkUpdater;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        jdbcSchemaBasedLinkUpdater.update();
    }
}
