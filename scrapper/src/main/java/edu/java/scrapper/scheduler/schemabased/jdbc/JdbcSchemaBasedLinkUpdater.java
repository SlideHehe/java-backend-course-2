package edu.java.scrapper.scheduler.schemabased.jdbc;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcTgChatDao;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.schemabased.SchemaBasedLinkUpdater;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JdbcSchemaBasedLinkUpdater extends SchemaBasedLinkUpdater {
    public JdbcSchemaBasedLinkUpdater(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcLinkDao jdbcLinkDao,
        BotClient botClient,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jdbcTgChatDao, jdbcLinkDao, botClient, applicationConfig, resourceUpdaters);
    }
}
