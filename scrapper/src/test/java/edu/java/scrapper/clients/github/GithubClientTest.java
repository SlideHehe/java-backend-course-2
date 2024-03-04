package edu.java.scrapper.clients.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.clients.ClientFactory;
import edu.java.scrapper.configurations.ApplicationConfig;
import edu.java.scrapper.configurations.ApplicationConfig.Client;
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
                        "html_url": "https://github.com/SlideHehe/java-backend-course-2",
                        "updated_at": "%s"
                    }
                    """.formatted(offsetDateTime))));
        GithubClient githubClient = ClientFactory.createGithubclient(applicationConfig);
        GithubRepository expectedRepository = new GithubRepository(
            "https://github.com/SlideHehe/java-backend-course-2",
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
