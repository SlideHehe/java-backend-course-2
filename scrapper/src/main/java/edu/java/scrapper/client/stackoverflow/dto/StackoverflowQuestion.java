package edu.java.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackoverflowQuestion(
    List<Item> items
) {
    public record Item(
        String title,
        String link,

        @JsonAlias("last_activity_date")
        OffsetDateTime lastActivityDate
    ) {
    }
}
