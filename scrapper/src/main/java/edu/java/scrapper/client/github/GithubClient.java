package edu.java.scrapper.client.github;

import edu.java.scrapper.client.github.dto.GithubCommit;
import edu.java.scrapper.client.github.dto.GithubPullRequest;
import edu.java.scrapper.client.github.dto.GithubRepository;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GithubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GithubRepository getRepository(@PathVariable String owner, @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/pulls")
    List<GithubPullRequest> getPullRequests(@PathVariable String owner, @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/commits")
    List<GithubCommit> getCommits(@PathVariable String owner, @PathVariable String repo);
}
