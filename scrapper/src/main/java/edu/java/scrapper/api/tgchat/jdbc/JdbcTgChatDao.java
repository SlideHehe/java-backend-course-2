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
    public List<TgChat> findAllByLinkId(Long linkId) {
        return jdbcClient.sql(
                "select chat.id, chat.created_at from chat join chat_link on chat.id = chat_link.chat_id "
                    + "where chat_link.link_id = ?")
            .param(linkId)
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
    public TgChat add(Long id) {
        return jdbcClient.sql("insert into chat (id) values (?) returning chat.id, chat.created_at")
            .param(id)
            .query(TgChat.class)
            .single();
    }

    @Override
    public TgChat remove(Long id) {
        return jdbcClient.sql("delete from chat where id = ? returning chat.id, chat.created_at")
            .param(id)
            .query(TgChat.class)
            .single();
    }
}
