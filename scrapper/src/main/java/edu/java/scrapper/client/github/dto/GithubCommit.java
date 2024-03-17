package edu.java.scrapper.client.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCommit(
    Commit commit
) {
    public record Commit(
        Author author,
        String message
    ) {
        public record Author(
            String name,
            OffsetDateTime date
        ) {
        }
    }
}
