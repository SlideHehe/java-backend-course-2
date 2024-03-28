package edu.java.scrapper.domain.tgchat.schemabased.jooq;

import edu.java.scrapper.domain.chatlink.schemabased.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.SchemaBasedTgChatService;

public class JooqSchemaBasedTgChatService extends SchemaBasedTgChatService {
    public JooqSchemaBasedTgChatService(JooqTgChatDao jooqTgChatDao, JooqChatLinkDao jooqChatLinkDao) {
        super(jooqTgChatDao, jooqChatLinkDao);
    }
}
