package edu.java.scrapper.domain.tgchat.schemabased.jdbc;

import edu.java.scrapper.domain.chatlink.schemabased.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.SchemaBasedTgChatService;

public class JdbcSchemaBasedTgChatService extends SchemaBasedTgChatService {
    public JdbcSchemaBasedTgChatService(JdbcTgChatDao jdbcTgChatDao, JdbcChatLinkDao jdbcChatLinkDao) {
        super(jdbcTgChatDao, jdbcChatLinkDao);
    }
}
