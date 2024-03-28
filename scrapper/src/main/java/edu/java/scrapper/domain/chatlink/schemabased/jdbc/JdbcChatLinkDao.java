package edu.java.scrapper.domain.chatlink.schemabased.jdbc;

import edu.java.scrapper.domain.chatlink.schemabased.ChatLink;
import edu.java.scrapper.domain.chatlink.schemabased.ChatLinkDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@SuppressWarnings("MultipleStringLiterals")
@RequiredArgsConstructor
@Repository
public class JdbcChatLinkDao implements ChatLinkDao {
    private final JdbcClient jdbcClient;

    @Override
    public List<ChatLink> findAll() {
        return jdbcClient.sql("select chat_link.chat_id, chat_link.link_id from chat_link")
            .query(ChatLink.class)
            .list();
    }

    @Override
    public Optional<ChatLink> findById(Long chatId, Long linkId) {
        return jdbcClient.sql("select chat_link.chat_id, chat_link.link_id from chat_link "
                              + "where chat_id = :chatId and link_id = :linkId")
            .param("chatId", chatId)
            .param("linkId", linkId)
            .query(ChatLink.class)
            .optional();
    }

    @Override
    public ChatLink add(Long chatId, Long linkId) {
        return jdbcClient.sql(
                "insert into chat_link (chat_id, link_id) values (:chatId, :linkId) "
                + "returning chat_link.chat_id, chat_link.link_id")
            .param("chatId", chatId)
            .param("linkId", linkId)
            .query(ChatLink.class)
            .single();
    }

    @Override
    public ChatLink remove(Long chatId, Long linkId) {
        return jdbcClient.sql(
                "delete from chat_link where chat_id = :chatId and link_id = :linkId "
                + "returning chat_link.chat_id, chat_link.link_id")
            .param("chatId", chatId)
            .param("linkId", linkId)
            .query(ChatLink.class)
            .single();
    }

    @Override
    public void removeDanglingLinks() {
        jdbcClient.sql("delete from link where not exists(select 1 from chat_link where chat_link.link_id = link.id)")
            .update();
    }
}
