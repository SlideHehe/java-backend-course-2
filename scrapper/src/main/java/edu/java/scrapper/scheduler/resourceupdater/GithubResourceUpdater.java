package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.domain.links.Link;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.github.dto.GithubCommit;
import edu.java.scrapper.client.github.dto.GithubPullRequest;
import edu.java.scrapper.client.github.dto.GithubRepository;
import edu.java.scrapper.scheduler.UpdateInfo;
import java.time.OffsetDateTime;
import java.util.List;
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
        return link.url().getHost().equals(ResourceUpdaterConstants.GITHUB_HOST) && link.type().equals(Type.GITHUB);
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

        log.info("Checking updates at: " + link.url());

        List<GithubPullRequest> githubPullRequests;
        List<GithubCommit> githubCommits;
        try {
            githubPullRequests = githubClient.getPullRequests(owner, repo);
            githubCommits = githubClient.getCommits(owner, repo);
        } catch (WebClientException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        String description = ResourceUpdaterConstants.GITHUB_UPDATE_RESPONSE.formatted(githubRepository.name())
                             + generatePullRequestsMessage(link, githubPullRequests, githubRepository.updatedAt())
                             + generateCommitsMessage(link, githubCommits, githubRepository.updatedAt());

        return Optional.of(
            createUpdateInfo(
                link,
                githubRepository.updatedAt(),
                githubPullRequests.size(),
                githubCommits.size(),
                description
            ));
    }

    private String generatePullRequestsMessage(
        Link link,
        List<GithubPullRequest> githubPullRequests,
        OffsetDateTime updatedAt
    ) {
        int pullRequestCount = link.pullRequestCount() == null ? 0 : link.pullRequestCount();

        if (githubPullRequests.size() == pullRequestCount) {
            return ResourceUpdaterConstants.EMPTY_STRING;
        }

        if (githubPullRequests.size() > pullRequestCount) {
            StringBuilder stringBuilder = new StringBuilder();
            githubPullRequests.stream()
                .filter(githubPullRequest -> githubPullRequest.createdAt().isAfter(updatedAt))
                .forEach(githubPullRequest -> stringBuilder.append(ResourceUpdaterConstants.GITHUB_NEW_PULL_REQUEST
                    .formatted(githubPullRequest.title())));

            return stringBuilder.toString();
        }

        return ResourceUpdaterConstants.GITHUB_PULL_REQUEST_CLOSED;
    }

    private String generateCommitsMessage(
        Link link,
        List<GithubCommit> githubCommits,
        OffsetDateTime updatedAt
    ) {
        int commitCount = link.commitCount() == null ? 0 : link.commitCount();

        if (githubCommits.size() == commitCount) {
            return ResourceUpdaterConstants.EMPTY_STRING;
        }

        if (githubCommits.size() > commitCount) {
            StringBuilder stringBuilder = new StringBuilder();
            githubCommits.stream()
                .filter(githubCommit -> githubCommit.commit().author().date().isAfter(updatedAt))
                .forEach(githubCommit -> stringBuilder.append(ResourceUpdaterConstants.GITHUB_NEW_COMMIT
                    .formatted(githubCommit.commit().author().name(), githubCommit.commit().message())));

            return stringBuilder.toString();
        }

        return ResourceUpdaterConstants.EMPTY_STRING;
    }

    private UpdateInfo createUpdateInfo(
        Link link,
        OffsetDateTime updatedAt,
        Integer pullRequestCount,
        Integer commitCount,
        String description
    ) {
        Link updatedLink = new Link(
            link.id(),
            link.url(),
            updatedAt,
            link.checkedAt(),
            link.type(),
            link.answerCount(),
            link.commentCount(),
            pullRequestCount,
            commitCount
        );
        return new UpdateInfo(updatedLink, description);
    }
}
