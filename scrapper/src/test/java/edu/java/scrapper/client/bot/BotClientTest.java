package edu.java.scrapper.client.bot;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.client.ClientFactory;
import edu.java.scrapper.client.bot.dto.LinkUpdateRequest;
import edu.java.scrapper.client.exchangefilterfunction.LinearRetryFilter;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.net.URI;
import java.time.Duration;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
class BotClientTest {
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
        when(applicationConfig.botClient()).thenReturn(client);
        LinearRetryFilter linearRetryFilter = new LinearRetryFilter(client);
        clientFactory = new ClientFactory(
            applicationConfig,
            linearRetryFilter,
            linearRetryFilter,
            linearRetryFilter
        );
    }

    @Test
    @DisplayName("Проверка успешного отправления обновления")
    void postUpdateSuccess() {
        // given
        stubFor(post(urlPathEqualTo("/updates"))
            .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson("""
                {
                    "url": "https://aboba.com/question",
                    "description": "description",
                    "tgChatIds": [1, 2, 3]
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)));
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                URI.create("https://aboba.com/question"),
                "description",
                List.of(1L, 2L, 3L)
            );
        BotClient botClient = clientFactory.createBotClient();

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
                URI.create("https://aboba.com/question"),
                "description",
                List.of(1L, 2L, 3L)
            );
        BotClient botClient = clientFactory.createBotClient();

        // when-then
        assertThatThrownBy(() -> botClient.createUpdate(linkUpdateRequest)).isInstanceOf(WebClientException.class);
    }

    @Test
    @DisplayName("Проверка retry политики")
    void clientRetry() {
        // given
        stubFor(post(urlPathEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(500)));
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(
                URI.create("https://aboba.com/question"),
                "description",
                List.of(1L, 2L, 3L)
            );
        BotClient botClient = clientFactory.createBotClient();

        // when-then
        assertThatThrownBy(() -> botClient.createUpdate(linkUpdateRequest)).isInstanceOf(WebClientException.class);
        WireMock.verify(4, postRequestedFor(urlPathEqualTo("/updates")));
    }
}
