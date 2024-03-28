package edu.java.scrapper.scheduler.schemabased.jooq;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.jooq.JooqLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.jooq.JooqTgChatDao;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.schemabased.SchemaBasedLinkUpdater;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JooqSchemaBasedLinkUpdater extends SchemaBasedLinkUpdater {
    public JooqSchemaBasedLinkUpdater(
        JooqTgChatDao jooqTgChatDao,
        JooqLinkDao jooqLinkDao,
        BotClient botClient,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jooqTgChatDao, jooqLinkDao, botClient, applicationConfig, resourceUpdaters);
    }
}
