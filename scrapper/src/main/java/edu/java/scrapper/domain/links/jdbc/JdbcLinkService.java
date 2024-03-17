package edu.java.scrapper.domain.links.jdbc;

import edu.java.scrapper.domain.chatlink.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.links.SchemaLinkService;
import org.springframework.stereotype.Service;

@Service
public class JdbcLinkService extends SchemaLinkService {
    public JdbcLinkService(JdbcLinkDao jdbcLinkDao, JdbcChatLinkDao jdbcChatLinkDao) {
        super(jdbcLinkDao, jdbcChatLinkDao);
    }
}
