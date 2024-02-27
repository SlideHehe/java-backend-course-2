package edu.java.scrapper.clients.github;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GithubClient {
    @GetExchange("/repos/{owner}/{repo}")
    GithubRepository getRepository(@PathVariable String owner, @PathVariable String repo);
}
