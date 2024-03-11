package edu.java.scrapper.api.tgchat.jdbc;

import edu.java.scrapper.api.tgchat.TgChat;
import edu.java.scrapper.api.tgchat.TgChatDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JdbcTgChatDao implements TgChatDao {
    private final JdbcClient jdbcClient;

    @Override
    public List<TgChat> findAll() {
        return jdbcClient.sql("select chat.id, chat.created_at from chat")
            .query(TgChat.class)
            .list();
    }

    @Override
    public Optional<TgChat> findById(Long id) {
        return jdbcClient.sql("select chat.id, chat.created_at from chat where id = ?")
            .param(id)
            .query(TgChat.class)
            .optional();
    }

    @Override
    public Optional<TgChat> add(Long id) {
        jdbcClient.sql("insert into chat (id) values (?)")
            .param(id)
            .update();

        return findById(id);
    }

    @Override
    public Optional<TgChat> remove(Long id) {
        Optional<TgChat> optionalTgChat = findById(id);

        jdbcClient.sql("delete from chat where id = ?")
            .param(id)
            .update();

        return optionalTgChat;
    }
}
