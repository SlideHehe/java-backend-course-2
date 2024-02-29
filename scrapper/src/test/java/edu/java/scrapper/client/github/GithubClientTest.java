package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.ApplicationConfig.Client;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.MediaType;
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
    void getRepositorySuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "id": 1,
                        "name": "java-backend-course-2",
                        "html_url": "https://github.com/SlideHehe/java-backend-course-2",
                        "pushed_at": "%s",
                        "updated_at": "%s"
                    }
                    """.formatted(offsetDateTime, offsetDateTime))));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);
        GithubRepository expectedRepository = new GithubRepository(
            1L,
            "java-backend-course-2",
            "https://github.com/SlideHehe/java-backend-course-2",
            offsetDateTime,
            offsetDateTime
        );

        // when
        GithubRepository actualRepository = githubClient.getRepository("SlideHehe", "java-backend-course-2");

        // then
        assertThat(actualRepository).isEqualTo(expectedRepository);
    }

    @Test
    void getRepositoryWrongBody() {
        // given
        stubFor(get(urlPathEqualTo("/repos/SlideHehe/java-backend-course-2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("notjson")));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> githubClient.getRepository("SlideHehe", "java-backend-course-2"))
            .isInstanceOf(DecodingException.class);
    }
}