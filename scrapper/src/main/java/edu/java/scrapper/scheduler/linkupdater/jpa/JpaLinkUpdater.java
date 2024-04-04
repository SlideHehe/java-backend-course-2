package edu.java.scrapper.scheduler.linkupdater.jpa;

import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.LinkMapper;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.tgchat.jpa.Chat;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import edu.java.scrapper.scheduler.linkupdater.LinkUpdater;
import edu.java.scrapper.scheduler.linkupdater.UpdateInfo;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.updatesender.UpdateSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class JpaLinkUpdater implements LinkUpdater {
    private final JpaLinkRepository jpaLinkRepository;
    private final JpaChatRepository jpaChatRepository;
    private final UpdateSender updateSender;
    private final ApplicationConfig applicationConfig;
    private final List<ResourceUpdater> resourceUpdaters;

    @Override
    @Transactional
    public void update() {
        Long secondsAgo = applicationConfig.scheduler().forceCheckDelay().getSeconds();
        var links = jpaLinkRepository.findByCheckedMoreThanSomeSecondsAgo(secondsAgo).stream()
            .map(LinkMapper::linkEntityToLinkSchema)
            .toList();

        if (links.isEmpty()) {
            return;
        }

        for (var link : links) {
            resourceUpdaters.stream()
                .filter(resourceUpdater -> resourceUpdater.supports(link))
                .findAny()
                .flatMap(resourceUpdater -> resourceUpdater.updateResource(link))
                .ifPresent(this::processLink);
        }

        List<Long> linkIds = links.stream()
            .map(Link::id)
            .toList();
        jpaLinkRepository.updateCheckedAtByIdIn(linkIds);
    }

    private void processLink(UpdateInfo updateInfo) {
        var link = updateInfo.link();
        jpaLinkRepository.updateUpdatedAtById(link.updatedAt(), link.id());
        jpaLinkRepository.updateCountersById(
            link.answerCount(),
            link.commentCount(),
            link.pullRequestCount(),
            link.commitCount(),
            link.id()
        );
        sendUpdate(updateInfo);
    }

    private void sendUpdate(UpdateInfo updateInfo) {
        List<Long> chatIds = jpaChatRepository.findByLinks_Id(updateInfo.link().id()).stream()
            .map(Chat::getId)
            .toList();

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(updateInfo.link().url(), updateInfo.description(), chatIds);
        updateSender.send(linkUpdateRequest);
    }
}
