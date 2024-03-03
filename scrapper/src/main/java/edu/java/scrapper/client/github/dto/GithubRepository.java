package edu.java.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepository(
    @JsonAlias("html_url")
    String url,

    @JsonAlias("updated_at")
    OffsetDateTime updatedAt
) {
}
