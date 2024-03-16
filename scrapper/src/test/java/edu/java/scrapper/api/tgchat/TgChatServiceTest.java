package edu.java.scrapper.api.tgchat;

import edu.java.scrapper.api.exception.ChatAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TgChatServiceTest {

    @Test
    @DisplayName("Регистрация чата")
    void registerChat() {
        // given
        TgChatService chatService = new TgChatService();

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.registerChat(1L));
    }

    @Test
    @DisplayName("Регистрация уже зарегистрированного чата")
    void registerRegisteredChat() {
        // given
        TgChatService chatService = new TgChatService();
        chatService.registerChat(1L);

        // when-then
        assertThatThrownBy(() -> chatService.registerChat(1L))
            .isInstanceOf(ChatAlreadyExistsException.class)
            .hasMessage("Указанный чат уже зарегестрирован");
    }

    @Test
    @DisplayName("Удаление чата")
    void deleteChat() {
        // given
        TgChatService chatService = new TgChatService();
        chatService.registerChat(1L);

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.deleteChat(1L));
    }

    @Test
    @DisplayName("Удаление незарегистрированного чата")
    void deleteNonRegisteredChat() {
        // given
        TgChatService chatService = new TgChatService();

        // when-then
        assertThatThrownBy(() -> chatService.deleteChat(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не существует");
    }
}
