package edu.java.bot.telegram.linkhandler;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkHandlerTest {
    @Test
    @DisplayName("Проверка поддержки обработки stackoverlow с допустимой ссылкой")
    void stackoverflowCanHandleCorrectLink() {
        // given
        StackoverflowLinkHandler stackoverflowLinkHandler = new StackoverflowLinkHandler();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could"
        );

        // when
        boolean actual = stackoverflowLinkHandler.canHandle(uri);

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://stakoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could",
        "https://stackoverflow.com"
    })
    @DisplayName("Проверка поддержки обработки stackoverflow с недопустимыми ссылкой")
    void stackoverflowCanHandleIncorrectLinks(String link) {
        // given
        StackoverflowLinkHandler stackoverflowLinkHandler = new StackoverflowLinkHandler();
        URI uri = URI.create(link);

        // when
        boolean actual = stackoverflowLinkHandler.canHandle(uri);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Проверка поддержки обработки github с допустимой ссылкой")
    void githubCanHandleCorrectLink() {
        // given
        GithubLinkHandler githubLinkHandler = new GithubLinkHandler();
        URI uri = URI.create(
            "https://github.com/SlideHehe/java-backend-course-2"
        );

        // when
        boolean actual = githubLinkHandler.canHandle(uri);

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://githb.com/SlideHehe/java-backend-course-2",
        "https://github.com"
    })
    @DisplayName("Проверка поддеркжи обработки github с недопустимыми ссылкой")
    void githubCanHandleIncorrectLinks(String link) {
        // given
        GithubLinkHandler githubLinkHandler = new GithubLinkHandler();
        URI uri = URI.create(link);

        // when
        boolean actual = githubLinkHandler.canHandle(uri);

        // then
        assertThat(actual).isFalse();
    }
}
