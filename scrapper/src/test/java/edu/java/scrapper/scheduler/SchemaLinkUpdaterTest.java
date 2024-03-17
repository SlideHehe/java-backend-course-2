package edu.java.scrapper.scheduler;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.Link;
import edu.java.scrapper.domain.links.LinkDao;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.tgchat.TgChatDao;
import edu.java.scrapper.scheduler.resourceupdater.GithubResourceUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaLinkUpdaterTest {
    @Mock
    TgChatDao tgChatDao;
    @Mock
    LinkDao linkDao;
    @Mock
    BotClient botClient;
    @Mock
    ApplicationConfig applicationConfig;
    @Mock
    GithubResourceUpdater githubResourceUpdater;

    @Test
    @DisplayName("Проверка обработки ссылки в методе update")
    void updateTest() {
        // given
        URI uri = URI.create("https://aboba.com");
        OffsetDateTime time = OffsetDateTime.now();
        List<Link> links = List.of(
            new Link(1L, uri, time, time, Type.GITHUB, null, null, null, null),
            new Link(2L, uri, time, time, Type.GITHUB, null, null, null, null),
            new Link(3L, uri, time, time, Type.GITHUB, null, null, null, null)
        );
        Duration duration = Duration.ofSeconds(10L);
        when(applicationConfig.scheduler()).thenReturn(new ApplicationConfig.Scheduler(false, duration, duration));
        when(linkDao.findCheckedMoreThanSomeSecondsAgo(10L)).thenReturn(links);
        when(githubResourceUpdater.supports(new Link(
            1L,
            uri,
            time,
            time,
            Type.GITHUB,
            null,
            null,
            null,
            null
        ))).thenReturn(true);
        SchemaLinkUpdater linkUpdater = new SchemaLinkUpdater(
            tgChatDao,
            linkDao,
            botClient,
            applicationConfig,
            List.of(githubResourceUpdater)
        );

        // when
        linkUpdater.update();

        // then
        verify(githubResourceUpdater, times(1)).updateResource(any());
    }

}
