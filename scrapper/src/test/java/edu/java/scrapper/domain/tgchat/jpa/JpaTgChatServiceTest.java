package edu.java.scrapper.domain.tgchat.jpa;

import edu.java.scrapper.domain.exception.ChatAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
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
class JpaTgChatServiceTest {
    @Mock
    JpaLinkRepository linkRepository;
    @Mock
    JpaChatRepository chatRepository;
    @InjectMocks
    JpaTgChatService chatService;

    @Test
    @DisplayName("Регистрация чата")
    void registerChat() {
        // given
        when(chatRepository.existsById(1L)).thenReturn(false);

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.registerChat(1L));
    }

    @Test
    @DisplayName("Регистрация уже зарегистрированного чата")
    void registerRegisteredChat() {
        // given
        when(chatRepository.existsById(1L)).thenReturn(true);

        // when-then
        assertThatThrownBy(() -> chatService.registerChat(1L))
            .isInstanceOf(ChatAlreadyExistsException.class)
            .hasMessage("Указанный чат уже зарегистрирован");
    }

    @Test
    @DisplayName("Удаление чата")
    void deleteChat() {
        // given
        when(chatRepository.existsById(1L)).thenReturn(true);
        chatService.deleteChat(1L);

        // when-then
        assertThatNoException().isThrownBy(() -> chatService.deleteChat(1L));
    }

    @Test
    @DisplayName("Удаление незарегистрированного чата")
    void deleteNonRegisteredChat() {
        // given
        when(chatRepository.existsById(1L)).thenReturn(false);

        // when-then
        assertThatThrownBy(() -> chatService.deleteChat(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не существует");
    }

}
