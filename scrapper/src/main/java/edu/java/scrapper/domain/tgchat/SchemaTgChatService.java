package edu.java.scrapper.domain.tgchat;

import edu.java.scrapper.domain.chatlink.ChatLinkDao;
import edu.java.scrapper.domain.exception.ChatAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class SchemaTgChatService implements TgChatService {
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
