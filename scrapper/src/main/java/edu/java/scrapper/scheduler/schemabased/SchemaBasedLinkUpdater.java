package edu.java.scrapper.scheduler.schemabased;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.schemabased.LinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.TgChat;
import edu.java.scrapper.domain.tgchat.schemabased.TgChatDao;
import edu.java.scrapper.scheduler.LinkUpdater;
import edu.java.scrapper.scheduler.UpdateInfo;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientException;

@Slf4j
@RequiredArgsConstructor
public class SchemaBasedLinkUpdater implements LinkUpdater {
    private final TgChatDao tgChatDao;
    private final LinkDao linkDao;
    private final BotClient botClient;
    private final ApplicationConfig applicationConfig;
    private final List<ResourceUpdater> resourceUpdaters;

    @Transactional
    @Override
    public void update() {
        Long secondsAgo = applicationConfig.scheduler().forceCheckDelay().getSeconds();
        List<Link> links = linkDao.findCheckedMoreThanSomeSecondsAgo(secondsAgo);

        for (Link link : links) {
            resourceUpdaters.stream()
                .filter(resourceUpdater -> resourceUpdater.supports(link))
                .findAny()
                .flatMap(resourceUpdater -> resourceUpdater.updateResource(link))
                .ifPresent(this::processLink);
        }

        List<Long> linkIds = links.stream()
            .map(Link::id)
            .toList();
        linkDao.updateCheckedTimestamp(linkIds);
    }

    private void processLink(UpdateInfo updateInfo) {
        Link link = updateInfo.link();
        linkDao.updateUpdatedTimestamp(link.id(), link.updatedAt());
        linkDao.updateCounters(
            link.id(),
            link.answerCount(),
            link.commentCount(),
            link.pullRequestCount(),
            link.commitCount()
        );
        sendUpdate(updateInfo);
    }

    private void sendUpdate(UpdateInfo updateInfo) {
        List<Long> chatIds = tgChatDao.findAllByLinkId(updateInfo.link().id())
            .stream()
            .map(TgChat::id)
            .toList();

        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(updateInfo.link().url(), updateInfo.description(), chatIds);

        try {
            botClient.createUpdate(linkUpdateRequest);
        } catch (WebClientException e) {
            log.error(e.getMessage());
        }
    }

}
