package edu.java.scrapper.scheduler.linkupdater.schemabased.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcTgChatDao;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.linkupdater.schemabased.SchemaBasedLinkUpdater;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import java.util.List;

public class JdbcSchemaBasedLinkUpdater extends SchemaBasedLinkUpdater {
    public JdbcSchemaBasedLinkUpdater(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcLinkDao jdbcLinkDao,
        UpdateProducer updateProducer,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jdbcTgChatDao, jdbcLinkDao, updateProducer, applicationConfig, resourceUpdaters);
    }
}
