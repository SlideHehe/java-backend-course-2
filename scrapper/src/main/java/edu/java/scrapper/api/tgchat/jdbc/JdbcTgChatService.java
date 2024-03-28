package edu.java.scrapper.api.tgchat.jdbc;

import edu.java.scrapper.api.chatlink.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.api.exception.ChatAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import edu.java.scrapper.api.tgchat.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final JdbcTgChatDao jdbcTgChatDao;
    private final JdbcChatLinkDao jdbcChatLinkDao;

    @Override
    public void registerChat(Long id) {
        jdbcTgChatDao.findById(id).ifPresent(ignored -> {
            throw new ChatAlreadyExistsException("Указанный чат уже зарегистрирован");
        });
        jdbcTgChatDao.add(id);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        jdbcTgChatDao.findById(id)
            .ifPresentOrElse(ignored -> jdbcTgChatDao.remove(id), () -> {
                throw new ResourceNotFoundException("Указанный чат не существует");
            });
        jdbcChatLinkDao.removeDanglingLinks();
    }
}
