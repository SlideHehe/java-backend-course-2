package edu.java.scrapper.scheduler.linkupdater.schemabased.jooq;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.schemabased.jooq.JooqLinkDao;
import edu.java.scrapper.domain.tgchat.schemabased.jooq.JooqTgChatDao;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.linkupdater.schemabased.SchemaBasedLinkUpdater;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import java.util.List;

public class JooqSchemaBasedLinkUpdater extends SchemaBasedLinkUpdater {
    public JooqSchemaBasedLinkUpdater(
        JooqTgChatDao jooqTgChatDao,
        JooqLinkDao jooqLinkDao,
        UpdateProducer updateProducer,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jooqTgChatDao, jooqLinkDao, updateProducer, applicationConfig, resourceUpdaters);
    }
}
