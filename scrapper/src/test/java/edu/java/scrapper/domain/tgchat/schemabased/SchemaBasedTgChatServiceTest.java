package edu.java.scrapper.domain.tgchat.schemabased;

import edu.java.scrapper.domain.chatlink.schemabased.ChatLinkDao;
import edu.java.scrapper.domain.exception.ChatAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
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
class SchemaBasedTgChatServiceTest {
    @Mock
    TgChatDao tgChatDao;

    @Mock
    ChatLinkDao chatLinkDao;

    @InjectMocks
    SchemaBasedTgChatService chatService;

    @Test
    @DisplayName("Регистрация чата")
    void registerChat() {
        // given
        when(tgChatDao.findById(1L)).thenReturn(Optional.empty());

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.registerChat(1L));
    }

    @Test
    @DisplayName("Регистрация уже зарегистрированного чата")
    void registerRegisteredChat() {
        // given
        when(tgChatDao.findById(1L)).thenReturn(Optional.of(new TgChat(1L, OffsetDateTime.now())));

        // when-then
        assertThatThrownBy(() -> chatService.registerChat(1L))
            .isInstanceOf(ChatAlreadyExistsException.class)
            .hasMessage("Указанный чат уже зарегистрирован");
    }

    @Test
    @DisplayName("Удаление чата")
    void deleteChat() {
        // given
        when(tgChatDao.findById(1L)).thenReturn(Optional.of(new TgChat(1L, OffsetDateTime.now())));
        chatService.deleteChat(1L);

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.deleteChat(1L));
    }

    @Test
    @DisplayName("Удаление незарегистрированного чата")
    void deleteNonRegisteredChat() {
        // given
        when(tgChatDao.findById(1L)).thenReturn(Optional.empty());

        // when-then
        assertThatThrownBy(() -> chatService.deleteChat(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не существует");
    }
}
