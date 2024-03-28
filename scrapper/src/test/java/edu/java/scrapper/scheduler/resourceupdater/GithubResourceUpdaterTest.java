package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.github.dto.GithubCommit;
import edu.java.scrapper.client.github.dto.GithubPullRequest;
import edu.java.scrapper.client.github.dto.GithubRepository;
import edu.java.scrapper.scheduler.UpdateInfo;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubResourceUpdaterTest {
    @Mock
    GithubClient githubClient;
    @InjectMocks
    GithubResourceUpdater githubResourceUpdater;

    @Test
    @DisplayName("Проверка метода supports для подходящей ссылки")
    void supportsTrue() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        Link link = new Link(
            1L,
            URI.create("https://github.com/SlideHehe/java-backend-course-2"),
            time,
            time,
            Type.GITHUB,
            null,
            null,
            null,
            null
        );

        // when-then
        assertThat(githubResourceUpdater.supports(link)).isTrue();
    }

    @Test
    @DisplayName("Проверка метода supports для неподходящей ссылки")
    void supportsFalse() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        Link link = new Link(
            1L,
            URI.create("https://github.com/java-backend-course-2"),
            time,
            time,
            Type.GITHUB,
            null,
            null,
            null,
            null
        );

        // when-then
        assertThat(githubResourceUpdater.supports(link)).isFalse();
    }

    @Test
    @DisplayName("Проверка получения добавления пулл реквестов")
    void getUpdatesNewPullRequest() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create("https://github.com/SlideHehe/java-backend-course-2");
        Link link =
            new Link(1L, uri, time.minusMinutes(10), time, Type.GITHUB, null, null, null, null);
        GithubRepository repository =
            new GithubRepository(
                "java-backend-course-2",
                "https://github.com/SlideHehe/java-backend-course-2",
                time.minusMinutes(5L)
            );
        List<GithubPullRequest> githubPullRequestList = List.of(
            new GithubPullRequest("hello", time)
        );
        when(githubClient.getRepository("SlideHehe", "java-backend-course-2")).thenReturn(repository);
        when(githubClient.getPullRequests("SlideHehe", "java-backend-course-2")).thenReturn(githubPullRequestList);
        when(githubClient.getCommits("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.GITHUB, null, null, 1, 0),
                ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE.formatted("java-backend-course-2")
                + ResourceUpdaterConstants.GITHUB_NEW_PULL_REQUEST.formatted("hello")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = githubResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения закрытия пулл реквестов")
    void getUpdatesPullRequestClosed() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create("https://github.com/SlideHehe/java-backend-course-2");
        Link link =
            new Link(1L, uri, time.minusMinutes(10), time, Type.GITHUB, null, null, 1, null);
        GithubRepository repository =
            new GithubRepository(
                "java-backend-course-2",
                "https://github.com/SlideHehe/java-backend-course-2",
                time.minusMinutes(5L)
            );

        when(githubClient.getRepository("SlideHehe", "java-backend-course-2")).thenReturn(repository);
        when(githubClient.getPullRequests("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        when(githubClient.getCommits("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.GITHUB, null, null, 0, 0),
                ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE.formatted("java-backend-course-2")
                + ResourceUpdaterConstants.GITHUB_PULL_REQUEST_CLOSED
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = githubResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения нового коммита")
    void getUpdatesNewCommit() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create("https://github.com/SlideHehe/java-backend-course-2");
        Link link =
            new Link(1L, uri, time.minusMinutes(10), time, Type.GITHUB, null, null, null, null);
        GithubRepository repository =
            new GithubRepository(
                "java-backend-course-2",
                "https://github.com/SlideHehe/java-backend-course-2",
                time.minusMinutes(5L)
            );
        List<GithubCommit> githubCommits = List.of(
            new GithubCommit(new GithubCommit.Commit(new GithubCommit.Commit.Author("SlideHehe", time), "message"))
        );
        when(githubClient.getRepository("SlideHehe", "java-backend-course-2")).thenReturn(repository);
        when(githubClient.getPullRequests("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        when(githubClient.getCommits("SlideHehe", "java-backend-course-2")).thenReturn(githubCommits);
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.GITHUB, null, null, 0, 1),
                ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE.formatted("java-backend-course-2")
                + ResourceUpdaterConstants.GITHUB_NEW_COMMIT.formatted("SlideHehe", "message")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = githubResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения необрабатываемого обновления")
    void getUpdatesUnknown() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create("https://github.com/SlideHehe/java-backend-course-2");
        Link link =
            new Link(1L, uri, time.minusMinutes(10), time, Type.GITHUB, null, null, null, null);
        GithubRepository repository =
            new GithubRepository(
                "java-backend-course-2",
                "https://github.com/SlideHehe/java-backend-course-2",
                time.minusMinutes(5L)
            );
        when(githubClient.getRepository("SlideHehe", "java-backend-course-2")).thenReturn(repository);
        when(githubClient.getPullRequests("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        when(githubClient.getCommits("SlideHehe", "java-backend-course-2")).thenReturn(List.of());
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.GITHUB, null, null, 0, 0),
                ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE.formatted("java-backend-course-2")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = githubResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Отсутствия обновления для ссылки")
    void noUpdatesForLink() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create("https://github.com/SlideHehe/java-backend-course-2");
        Link link = new Link(1L, uri, time, time, Type.GITHUB, null, null, null, null);
        GithubRepository repository =
            new GithubRepository("java-backend-course-2", "https://github.com/SlideHehe/java-backend-course-2", time);
        when(githubClient.getRepository("SlideHehe", "java-backend-course-2")).thenReturn(repository);
        Optional<UpdateInfo> expectedUpdateInfo = Optional.empty();

        // when
        Optional<UpdateInfo> actualUpdateInfo = githubResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }
}
