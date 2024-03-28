package edu.java.scrapper.api.tgchat.jdbc;

import edu.java.scrapper.api.chatlink.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.api.exception.ChatAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import edu.java.scrapper.api.tgchat.TgChat;
import edu.java.scrapper.api.tgchat.TgChatService;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcTgChatServiceTest {
    @Mock
    JdbcTgChatDao jdbcTgChatDao;

    @Mock
    JdbcChatLinkDao jdbcChatLinkDao;

    @InjectMocks
    JdbcTgChatService chatService;

    @Test
    @DisplayName("Регистрация чата")
    void registerChat() {
        // given
        when(jdbcTgChatDao.findById(1L)).thenReturn(Optional.empty());

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.registerChat(1L));
    }

    @Test
    @DisplayName("Регистрация уже зарегистрированного чата")
    void registerRegisteredChat() {
        // given
        when(jdbcTgChatDao.findById(1L)).thenReturn(Optional.of(new TgChat(1L, OffsetDateTime.now())));

        // when-then
        assertThatThrownBy(() -> chatService.registerChat(1L))
            .isInstanceOf(ChatAlreadyExistsException.class)
            .hasMessage("Указанный чат уже зарегистрирован");
    }

    @Test
    @DisplayName("Удаление чата")
    void deleteChat() {
        // given
        when(jdbcTgChatDao.findById(1L)).thenReturn(Optional.of(new TgChat(1L, OffsetDateTime.now())));
        chatService.deleteChat(1L);

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.deleteChat(1L));
    }

    @Test
    @DisplayName("Удаление незарегистрированного чата")
    void deleteNonRegisteredChat() {
        // given
        when(jdbcTgChatDao.findById(1L)).thenReturn(Optional.empty());

        // when-then
        assertThatThrownBy(() -> chatService.deleteChat(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не существует");
    }
}
