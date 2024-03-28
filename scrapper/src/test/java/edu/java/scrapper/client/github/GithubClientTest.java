package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.github.dto.GithubCommit;
import edu.java.scrapper.client.github.dto.GithubPullRequest;
import edu.java.scrapper.client.github.dto.GithubRepository;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.ApplicationConfig.Client;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
public class GithubClientTest {
    @Mock
    ApplicationConfig applicationConfig;

    @Mock
    Client client;

    @BeforeEach
    void setupConfig() {
        when(client.githubApiUrl()).thenReturn("http://localhost:8080");
        when(applicationConfig.client()).thenReturn(client);
    }

    @Test
    @DisplayName("Запрос получения репозитория")
    void getRepositorySuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "name": "java-backend-course-2",
                        "html_url": "https://github.com/SlideHehe/java-backend-course-2",
                        "updated_at": "%s"
                    }
                    """.formatted(offsetDateTime))));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);
        GithubRepository expectedRepository = new GithubRepository(
            "java-backend-course-2",
            "https://github.com/SlideHehe/java-backend-course-2",
            offsetDateTime
        );

        // when
        GithubRepository actualRepository = githubClient.getRepository("SlideHehe", "java-backend-course-2");

        // then
        assertThat(actualRepository).isEqualTo(expectedRepository);
    }

    @Test
    @DisplayName("Запрос получения пул реквестов")
    void getPullRequestsSuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2/pulls"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    [
                        {
                            "title": "homework1",
                            "created_at": "%s"
                        },
                        {
                            "title": "homework2",
                            "created_at": "%s"
                        }
                    ]
                    """.formatted(offsetDateTime, offsetDateTime))));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);
        List<GithubPullRequest> expectedPullRequests = List.of(
            new GithubPullRequest("homework1", offsetDateTime),
            new GithubPullRequest("homework2", offsetDateTime)
        );

        // when
        List<GithubPullRequest> actualPullRequests = githubClient.getPullRequests("SlideHehe", "java-backend-course-2");

        // then
        assertThat(actualPullRequests).isEqualTo(expectedPullRequests);
    }

    @Test
    @DisplayName("Запрос получения коммитов")
    void getCommitsSuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2/commits"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    [
                        {
                            "commit": {
                                "message": "hello1",
                                "author": {
                                    "name": "SlideHehe",
                                    "date": "%s"
                                }
                            }
                        },
                        {
                            "commit": {
                                "message": "hello2",
                                "author": {
                                    "name": "SlideHehe",
                                    "date": "%s"
                                }
                            }
                        }
                    ]
                    """.formatted(offsetDateTime, offsetDateTime))));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);
        List<GithubCommit> expectedCommits = List.of(
            new GithubCommit(new GithubCommit.Commit(
                new GithubCommit.Commit.Author("SlideHehe", offsetDateTime),
                "hello1"
            )),
            new GithubCommit(new GithubCommit.Commit(
                new GithubCommit.Commit.Author("SlideHehe", offsetDateTime),
                "hello2"
            ))
        );

        // when
        List<GithubCommit> actualCommits = githubClient.getCommits("SlideHehe", "java-backend-course-2");

        // then
        assertThat(actualCommits).isEqualTo(expectedCommits);
    }

    @Test
    @DisplayName("Запрос с клиентской ошибкой")
    void getRepositoryError() {
        // given
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2"))
            .willReturn(aResponse()
                .withStatus(418)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> githubClient.getRepository("SlideHehe", "java-backend-course-2"))
            .isInstanceOf(WebClientException.class);
    }
}
