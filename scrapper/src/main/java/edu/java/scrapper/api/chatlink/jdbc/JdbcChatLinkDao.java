package edu.java.scrapper.api.chatlink.jdbc;

import edu.java.scrapper.api.chatlink.ChatLink;
import edu.java.scrapper.api.chatlink.ChatLinkDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
        return jdbcClient.sql(
                "select chat_link.chat_id, chat_link.link_id from chat_link where chat_id = ? and link_id = ?")
            .params(chatId, linkId)
            .query(ChatLink.class)
            .optional();
    }

    @Override
    public Optional<ChatLink> add(Long chatId, Long linkId) {
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (?, ?)")
            .params(chatId, linkId)
            .update();

        return findById(chatId, linkId);
    }

    @Override
    public Optional<ChatLink> remove(Long chatId, Long linkId) {
        Optional<ChatLink> optionalChatLink = findById(chatId, linkId);

        jdbcClient.sql("delete  from chat_link where chat_id = ? and link_id = ?")
            .params(chatId, linkId)
            .update();

        return optionalChatLink;
    }
}
