package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.net.URI;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
public class BotClientTest {
    @Mock
    ApplicationConfig applicationConfig;

    @Mock
    ApplicationConfig.Client client;

    @BeforeEach
    void setupConfig() {
        when(client.botApiUrl()).thenReturn("http://localhost:8080");
        when(applicationConfig.client()).thenReturn(client);
    }

    @Test
    @DisplayName("Проверка успешного отправления обновления")
    void postUpdateSuccess() {
        // given
        stubFor(post(urlPathEqualTo("/updates"))
            .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson("""
                {
                    "id": 1,
                    "url": "https://aboba.com/question",
                    "description": "description",
                    "tgChatIds": [1, 2, 3]
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)));
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                1L,
                URI.create("https://aboba.com/question"),
                "description",
                List.of(1L, 2L, 3L)
            );
        BotClient botClient = ClientFactory.createBotClient(applicationConfig);

        // when-then
        assertThatNoException().isThrownBy(() -> botClient.createUpdate(linkUpdateRequest));
    }

    @Test
    @DisplayName("Запрос с клиентской ошибкой")
    void postUpdateError() {
        // given
        stubFor(post(urlPathEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(400)));
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                1L,
                URI.create("https://aboba.com/question"),
                "description",
                List.of(1L, 2L, 3L)
            );
        BotClient botClient = ClientFactory.createBotClient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> botClient.createUpdate(linkUpdateRequest)).isInstanceOf(WebClientException.class);
    }
}
