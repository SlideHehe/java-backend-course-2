package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.exchangefilterfunction.LinearRetryFilter;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowAnswers;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowComments;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
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
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
class StackoverflowClientTest {
    @Mock
    ApplicationConfig applicationConfig;

    @Mock
    ApplicationConfig.Client client;
    @Mock
    ApplicationConfig.Client.Retry retry;
    ClientFactory clientFactory;

    @BeforeEach
    void setupConfig() {
        when(retry.maxAttempts()).thenReturn(3);
        when(retry.initialBackoff()).thenReturn(Duration.ofSeconds(1));
        when(retry.retryableCodes()).thenReturn(Set.of(500));
        when(client.retry()).thenReturn(retry);
        when(client.baseUrl()).thenReturn("http://localhost:8080");
        when(applicationConfig.stackoverflowClient()).thenReturn(client);
        LinearRetryFilter linearRetryFilter = new LinearRetryFilter(client);
        clientFactory = new ClientFactory(
            applicationConfig,
            linearRetryFilter,
            linearRetryFilter,
            linearRetryFilter
        );
    }

    @Test
    @DisplayName("Запрос получения вопроса")
    void getQuestionSuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathMatching("/questions/1642028"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "items": [
                            {
                                "title": "what-is-the-operator-in-c-c",
                                "link": "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c",
                                "last_activity_date": "%s"
                            }
                        ]
                    }
                    """.formatted(offsetDateTime))));
        StackoverflowClient stackoverflowClient = clientFactory.createStackoverflowClient();
        StackoverflowQuestion expectedQuestion = new StackoverflowQuestion(
            List.of(new StackoverflowQuestion.Item(
                "what-is-the-operator-in-c-c",
                "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c",
                offsetDateTime
            ))
        );

        // when
        StackoverflowQuestion actualQuestion = stackoverflowClient.getQuestion(1642028L);

        // then
        assertThat(actualQuestion).isEqualTo(expectedQuestion);
    }

    @Test
    @DisplayName("Запрос получения ответов")
    void getAnswersSuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathMatching("/questions/1642028/answers"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "items": [
                            {
                                "owner": {
                                    "name": "aboba"
                                },
                                "creation_date": "%s"
                            }
                        ]
                    }
                    """.formatted(offsetDateTime))));
        StackoverflowClient stackoverflowClient = clientFactory.createStackoverflowClient();
        StackoverflowAnswers expectedAnswers = new StackoverflowAnswers(
            List.of(
                new StackoverflowAnswers.Item(new StackoverflowAnswers.Item.Owner("aboba"), offsetDateTime)
            )
        );

        // when
        StackoverflowAnswers actualAnswers = stackoverflowClient.getAnswers(1642028L);

        // then
        assertThat(actualAnswers).isEqualTo(expectedAnswers);
    }

    @Test
    @DisplayName("Запрос получения комментариев")
    void getCommentsSuccess() {
        // given
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        stubFor(get(urlPathMatching("/questions/1642028/comments"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "items": [
                            {
                                "owner": {
                                    "name": "aboba"
                                },
                                "creation_date": "%s"
                            }
                        ]
                    }
                    """.formatted(offsetDateTime))));
        StackoverflowClient stackoverflowClient = clientFactory.createStackoverflowClient();
        StackoverflowComments expectedComments = new StackoverflowComments(
            List.of(
                new StackoverflowComments.Item(new StackoverflowComments.Item.Owner("aboba"), offsetDateTime)
            )
        );

        // when
        StackoverflowComments actualComments = stackoverflowClient.getComments(1642028L);

        // then
        assertThat(actualComments).isEqualTo(expectedComments);
    }

    @Test
    @DisplayName("Запрос с клиентской ошибкой")
    void getQuestionError() {
        // given
        stubFor(get(urlPathMatching("/questions/1642028"))
            .willReturn(aResponse()
                .withStatus(418)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));
        StackoverflowClient stackoverflowClient = clientFactory.createStackoverflowClient();

        // when-then
        assertThatThrownBy(() -> stackoverflowClient.getQuestion(1642028L))
            .isInstanceOf(WebClientException.class);
    }

    @Test
    @DisplayName("Проверка retry политики")
    void clientRetry() {
        // given
        stubFor(get(urlPathMatching("/questions/1642028"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));
        StackoverflowClient stackoverflowClient = clientFactory.createStackoverflowClient();

        // when-then
        assertThatThrownBy(() -> stackoverflowClient.getQuestion(1642028L))
            .isInstanceOf(WebClientException.class);
        WireMock.verify(4, getRequestedFor(urlPathEqualTo("/questions/1642028")));
    }
}

