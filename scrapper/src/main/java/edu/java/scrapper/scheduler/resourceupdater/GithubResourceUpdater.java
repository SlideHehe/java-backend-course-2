package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.api.links.Link;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.github.dto.GithubRepository;
import edu.java.scrapper.scheduler.UpdateInfo;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubResourceUpdater implements ResourceUpdater {
    private static final Pattern REPOSITORY_PATTERN = Pattern.compile("/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+");
    private final GithubClient githubClient;

    @Override
    public boolean supports(Link link) {
        return supportsHostname(link) && supportsPath(link);
    }

    private boolean supportsHostname(Link link) {
        return link.url().getHost().equals(ResourceUpdaterConstants.GITHUB_HOST);
    }

    private boolean supportsPath(Link link) {
        String path = link.url().getPath();
        return REPOSITORY_PATTERN.matcher(path).find();
    }

    @Override
    public Optional<UpdateInfo> updateResource(Link link) {
        String[] splitPath = link.url().getPath().split("/");

        String owner = splitPath[1];
        String repo = splitPath[2];

        GithubRepository githubRepository;
        try {
            githubRepository = githubClient.getRepository(owner, repo);
        } catch (WebClientException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        if (githubRepository.updatedAt().equals(link.updatedAt())) {
            return Optional.empty();
        }

        UpdateInfo updateInfo = new UpdateInfo(
            link.id(),
            link.url(),
            ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE,
            githubRepository.updatedAt()
        );
        return Optional.of(updateInfo);
    }
}
