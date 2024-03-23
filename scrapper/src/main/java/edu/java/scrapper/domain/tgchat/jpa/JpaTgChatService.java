package edu.java.scrapper.domain.tgchat.jpa;

import edu.java.scrapper.domain.exception.ChatAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.tgchat.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class JpaTgChatService implements TgChatService {
    private final JpaChatRepository jpaChatRepository;
    private final JpaLinkRepository jpaLinkRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        if (jpaChatRepository.existsById(id)) {
            throw new ChatAlreadyExistsException("Указанный чат уже зарегистрирован");
        }
        Chat chat = new Chat();
        chat.setId(id);
        jpaChatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        if (!jpaChatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Указанный чат не существует");
        }
        jpaChatRepository.deleteById(id);
        jpaLinkRepository.deleteAllByChatsEmpty();
    }
}
