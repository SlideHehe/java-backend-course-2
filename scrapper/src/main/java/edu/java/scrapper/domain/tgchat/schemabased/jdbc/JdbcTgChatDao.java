package edu.java.scrapper.domain.tgchat.schemabased.jdbc;

import edu.java.scrapper.domain.tgchat.schemabased.TgChat;
import edu.java.scrapper.domain.tgchat.schemabased.TgChatDao;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@SuppressWarnings("MultipleStringLiterals")
@RequiredArgsConstructor
@Repository
public class JdbcTgChatDao implements TgChatDao {
    private final JdbcClient jdbcClient;

    @Override
    public List<TgChat> findAll() {
        return findAllBy("", Map.of());
    }

    @Override
    public List<TgChat> findAllByLinkId(Long linkId) {
        return findAllBy(
            "join chat_link on chat.id = chat_link.chat_id where chat_link.link_id = :linkId",
            Map.of("linkId", linkId)
        );
    }

    @Override
    public Optional<TgChat> findById(Long id) {
        return findBy(
            "where id = :id",
            Map.of("id", id)
        );
    }

    @Override
    public TgChat add(Long id) {
        return jdbcClient.sql("insert into chat (id) values (:chatId) returning chat.id, chat.created_at")
            .param("chatId", id)
            .query(TgChat.class)
            .single();
    }

    @Override
    public TgChat remove(Long id) {
        return jdbcClient.sql("delete from chat where id = :chatId returning chat.id, chat.created_at")
            .param("chatId", id)
            .query(TgChat.class)
            .single();
    }

    private List<TgChat> findAllBy(String criteria, Map<String, ?> params) {
        return jdbcClient.sql("select chat.id, chat.created_at from chat " + criteria)
            .params(params)
            .query(TgChat.class)
            .list();
    }

    private Optional<TgChat> findBy(String criteria, Map<String, ?> params) {
        return jdbcClient.sql("select chat.id, chat.created_at from chat " + criteria)
            .params(params)
            .query(TgChat.class)
            .optional();
    }
}
