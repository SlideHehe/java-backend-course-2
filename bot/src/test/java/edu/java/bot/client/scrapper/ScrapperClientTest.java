package edu.java.bot.client.scrapper;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.client.ClientFactory;
import edu.java.bot.client.scrapper.dto.AddLinkRequest;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.ListLinkResponse;
import edu.java.bot.client.scrapper.dto.RemoveLinkRequest;
import edu.java.bot.configuration.ApplicationConfig;
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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WireMockTest(httpPort = 8080)
public class ScrapperClientTest {
    @Mock
    ApplicationConfig applicationConfig;

    @Mock
    ApplicationConfig.Client client;

    @BeforeEach
    void setupConfig() {
        when(client.scrapperApiUrl()).thenReturn("http://localhost:8080");
        when(applicationConfig.client()).thenReturn(client);
    }

    @Test
    @DisplayName("Успешная регистрация чата")
    void successChatRegistration() {
        // given
        stubFor(post(urlPathEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)));
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when-then
        assertThatNoException().isThrownBy(() -> scrapperClient.registerChat(1L));
    }

    @Test
    @DisplayName("Успешное удаление чата")
    void successChatDeletion() {
        // given
        stubFor(delete(urlPathEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)));
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when-then
        assertThatNoException().isThrownBy(() -> scrapperClient.deleteChat(1L));
    }

    @Test
    @DisplayName("Получение списка отслеживаемых ссылок")
    void getFollowedLinks() {
        // given
        stubFor(get(urlPathEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "links": [
                            {
                                "id": 1,
                                "url": "https://aboba.com/1"
                            },
                            {
                                "id": 2,
                                "url": "https://aboba.com/2"
                            }
                        ],
                        "size": 2
                    }
                    """)));
        ListLinkResponse expectedList = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create("https://aboba.com/1")),
                new LinkResponse(2L, URI.create("https://aboba.com/2"))
            ),
            2
        );
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when
        ListLinkResponse actualList = scrapperClient.getFollowedLinks(1L);

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("Добавление новой ссылки")
    void addNewLink() {
        // given
        stubFor(post(urlPathEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson("""
                {
                    "link": "https://aboba.com"
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "id": 1,
                        "url": "https://aboba.com"
                    }
                    """)));
        AddLinkRequest request = new AddLinkRequest(URI.create("https://aboba.com"));
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://aboba.com"));
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when
        LinkResponse actualResponse = scrapperClient.addLink(1L, request);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Удаление ссылки")
    void removeLink() {
        // given
        stubFor(delete(urlPathEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson("""
                {
                    "link": "https://aboba.com"
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "id": 1,
                        "url": "https://aboba.com"
                    }
                    """)));
        RemoveLinkRequest request = new RemoveLinkRequest(URI.create("https://aboba.com"));
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://aboba.com"));
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when
        LinkResponse actualResponse = scrapperClient.removeLink(1L, request);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Запрос с клиентской ошибкой")
    void requestClientError() {
        // given
        stubFor(delete(urlPathEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(400)));
        ScrapperClient scrapperClient = ClientFactory.createScrapperClient(applicationConfig);

        // when-then
        assertThatThrownBy(() -> scrapperClient.deleteChat(1L)).isInstanceOf(WebClientException.class);
    }
}
