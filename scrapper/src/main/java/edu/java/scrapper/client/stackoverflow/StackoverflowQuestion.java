package edu.java.scrapper.client.stackoverflow;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackoverflowQuestion(
    List<Item> items
) {
    public record Item(
        @JsonAlias("question_id")
        Long questionId,

        String link,

        String title,

        @JsonAlias("answer_count")
        Integer answerCount,

        @JsonAlias("is_answered")
        Boolean isAnswered,

        @JsonAlias("last_activity_date")
        OffsetDateTime lastActivityDate,

        @JsonAlias("last_edit_date")
        OffsetDateTime lastEditDate
    ) {
    }
}
