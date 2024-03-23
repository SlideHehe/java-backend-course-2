package edu.java.scrapper.domain.links.schemabased.jdbc;

import edu.java.scrapper.domain.chatlink.schemabased.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.links.schemabased.SchemaBasedLinkService;

public class JdbcSchemaBasedLinkService extends SchemaBasedLinkService {
    public JdbcSchemaBasedLinkService(JdbcLinkDao jdbcLinkDao, JdbcChatLinkDao jdbcChatLinkDao) {
        super(jdbcLinkDao, jdbcChatLinkDao);
    }
}
