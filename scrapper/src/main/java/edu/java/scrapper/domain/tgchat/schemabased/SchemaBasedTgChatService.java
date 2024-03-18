package edu.java.scrapper.domain.tgchat.schemabased;

import edu.java.scrapper.domain.chatlink.schemabased.ChatLinkDao;
import edu.java.scrapper.domain.exception.ChatAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.tgchat.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class SchemaBasedTgChatService implements TgChatService {
    private final TgChatDao tgChatDao;
    private final ChatLinkDao chatLinkDao;

    @Override
    public void registerChat(Long id) {
        tgChatDao.findById(id).ifPresent(ignored -> {
            throw new ChatAlreadyExistsException("Указанный чат уже зарегистрирован");
        });
        tgChatDao.add(id);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        tgChatDao.findById(id)
            .ifPresentOrElse(ignored -> tgChatDao.remove(id), () -> {
                throw new ResourceNotFoundException("Указанный чат не существует");
            });
        chatLinkDao.removeDanglingLinks();
    }
}
