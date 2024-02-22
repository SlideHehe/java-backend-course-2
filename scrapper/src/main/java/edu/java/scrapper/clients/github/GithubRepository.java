package edu.java.scrapper.clients.github;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepository(
    Long id,

    String name,

    @JsonAlias("html_url")
    String url,

    @JsonAlias("pushed_at")
    OffsetDateTime pushedAt,

    @JsonAlias("updated_at")
    OffsetDateTime updatedAt
) {
}
