package edu.java.scrapper.clients.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.clients.ClientFactory;
import edu.java.scrapper.clients.stackoverflow.StackoverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackoverflowQuestion;
import edu.java.scrapper.configurations.ApplicationConfig;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
                                "link": "https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c",
                                "last_activity_date": "%s",
                            }
                        ]
                    }
                    """.formatted(offsetDateTime))));
        StackoverflowClient stackoverflowClient = ClientFactory.createStackoverflowClient(applicationConfig);
        StackoverflowQuestion expectedQuestion = new StackoverflowQuestion(
            List.of(new StackoverflowQuestion.Item(
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
}

