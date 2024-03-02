package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
public class StackoverflowClientTest {
    @Mock
    ApplicationConfig applicationConfig;

    @Mock
    ApplicationConfig.Client client;

    @BeforeEach
    void setupConfig() {
        when(client.stackoverflowApiUrl()).thenReturn("http://localhost:8080");
        when(applicationConfig.client()).thenReturn(client);
    }

    @Test
    @DisplayName("Успешный запрос")
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
                                "question_id": 1642028,
                                "link": "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c",
                                "title": "What is the &#39;--&gt;&#39; operator in C/C++?",
                                "answer_count": 26,
                                "is_answered": true,
                                "last_activity_date": "%s",
                                "last_edit_date": "%s"
                            }
                        ]
                    }
                    """.formatted(offsetDateTime, offsetDateTime))));
        StackoverflowClient stackoverflowClient = ClientFactory.createStackoverflowClient(applicationConfig);
        StackoverflowQuestion expectedQuestion = new StackoverflowQuestion(
            List.of(new StackoverflowQuestion.Item(
                1642028L,
                "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c",
                "What is the &#39;--&gt;&#39; operator in C/C++?",
                26,
                true,
                offsetDateTime,
                offsetDateTime
            ))
        );

        // when
        StackoverflowQuestion actualQuestion = stackoverflowClient.getQuestion(1642028L);

        // then
        assertThat(actualQuestion).isEqualTo(expectedQuestion);
    }

    @Test
    @DisplayName("Запрос с неверным телом ответа")
    void getQuestionWrongBody() {
        // given
        stubFor(get(urlPathMatching("/questions/1642028"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("notjson")));
        StackoverflowClient stackoverflowClient = ClientFactory.createStackoverflowClient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> stackoverflowClient.getQuestion(1642028L))
            .isInstanceOf(DecodingException.class);
    }

    @Test
    @DisplayName("Запрос с клиентской ошибкой")
    void getQuestionError() {
        // given
        stubFor(get(urlPathMatching("/questions/1642028"))
            .willReturn(aResponse()
                .withStatus(418)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("notjson")));
        StackoverflowClient stackoverflowClient = ClientFactory.createStackoverflowClient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> stackoverflowClient.getQuestion(1642028L))
            .isInstanceOf(WebClientException.class);
    }
}

