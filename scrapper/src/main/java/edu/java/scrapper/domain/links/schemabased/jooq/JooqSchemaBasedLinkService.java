package edu.java.scrapper.domain.links.schemabased.jooq;

import edu.java.scrapper.domain.chatlink.schemabased.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.links.schemabased.SchemaBasedLinkService;

public class JooqSchemaBasedLinkService extends SchemaBasedLinkService {
    public JooqSchemaBasedLinkService(JooqLinkDao jooqLinkDao, JooqChatLinkDao jooqChatLinkDao) {
        super(jooqLinkDao, jooqChatLinkDao);
    }
}
