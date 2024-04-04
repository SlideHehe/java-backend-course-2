package edu.java.scrapper.scheduler.linkupdater.jpa;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.LinkMapper;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.links.jpa.Link;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.GithubResourceUpdater;
import edu.java.scrapper.scheduler.updatesender.UpdateSender;
import java.time.Duration;
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
class JpaLinkUpdaterTest {
    @Mock
    JpaChatRepository chatRepository;
    @Mock
    JpaLinkRepository linkRepository;
    @Mock
    UpdateSender updateSender;
    @Mock
    ApplicationConfig applicationConfig;
    @Mock
    GithubResourceUpdater githubResourceUpdater;

    @Test
    @DisplayName("Проверка обработки ссылки в методе update")
    void update() {
        // given
        Link link1 = new Link();
        link1.setId(1L);
        Link link2 = new Link();
        link2.setId(2L);
        Link link3 = new Link();
        link3.setId(3L);
        List<Link> links = List.of(link1, link2, link3);
        Duration duration = Duration.ofSeconds(10L);
        when(applicationConfig.scheduler()).thenReturn(new ApplicationConfig.Scheduler(false, duration, duration));
        when(linkRepository.findByCheckedMoreThanSomeSecondsAgo(10L)).thenReturn(links);
        when(githubResourceUpdater.supports(LinkMapper.linkEntityToLinkSchema(link1))).thenReturn(true);
        JpaLinkUpdater linkUpdater = new JpaLinkUpdater(
            linkRepository,
            chatRepository,
            updateSender,
            applicationConfig,
            List.of(githubResourceUpdater)
        );

        // when
        linkUpdater.update();

        // then
        verify(githubResourceUpdater, times(1)).updateResource(any());
    }

}
