package edu.java.scrapper.scheduler.linkupdater.schemabased.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcTgChatDao;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.linkupdater.schemabased.SchemaBasedLinkUpdater;
import edu.java.scrapper.scheduler.updatesender.UpdateSender;
import java.util.List;

public class JdbcSchemaBasedLinkUpdater extends SchemaBasedLinkUpdater {
    public JdbcSchemaBasedLinkUpdater(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcLinkDao jdbcLinkDao,
        UpdateSender updateSender,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jdbcTgChatDao, jdbcLinkDao, updateSender, applicationConfig, resourceUpdaters);
    }
}
