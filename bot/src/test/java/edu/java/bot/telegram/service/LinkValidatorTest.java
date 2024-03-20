package edu.java.bot.telegram.service;

import java.net.URI;

import edu.java.bot.telegram.service.linkvalidator.GithubLinkValidator;
import edu.java.bot.telegram.service.linkvalidator.StackoverflowLinkValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class LinkValidatorTest {
    @Test
    @DisplayName("Проверка поддержки обработки stackoverlow с допустимой ссылкой")
    void stackoverflowCanHandleCorrectLink() {
        // given
        StackoverflowLinkValidator stackoverflowLinkHandler = new StackoverflowLinkValidator();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could"
        );

        // when
        boolean actual = stackoverflowLinkHandler.supports(uri);

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
        StackoverflowLinkValidator stackoverflowLinkHandler = new StackoverflowLinkValidator();
        URI uri = URI.create(link);

        // when
        boolean actual = stackoverflowLinkHandler.supports(uri);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Проверка поддержки обработки github с допустимой ссылкой")
    void githubCanHandleCorrectLink() {
        // given
        GithubLinkValidator githubLinkHandler = new GithubLinkValidator();
        URI uri = URI.create(
            "https://github.com/SlideHehe/java-backend-course-2"
        );

        // when
        boolean actual = githubLinkHandler.supports(uri);

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
        GithubLinkValidator githubLinkHandler = new GithubLinkValidator();
        URI uri = URI.create(link);

        // when
        boolean actual = githubLinkHandler.supports(uri);

        // then
        assertThat(actual).isFalse();
    }
}
