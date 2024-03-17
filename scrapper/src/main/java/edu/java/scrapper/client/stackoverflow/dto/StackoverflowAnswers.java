package edu.java.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.OffsetDateTime;
import java.util.List;

public record StackoverflowAnswers(
    List<Item> items
) {
    public record Item(
        Owner owner,
        @JsonAlias("creation_date")
        OffsetDateTime creationDate
    ) {
        public record Owner(
            @JsonAlias("display_name")
            String name
        ) {
        }
    }
}
