package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.api.links.Link;
import edu.java.scrapper.api.links.Type;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowAnswers;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowComments;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import edu.java.scrapper.scheduler.UpdateInfo;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@Slf4j
@RequiredArgsConstructor
public class StackoverflowResourceUpdater implements ResourceUpdater {
    private static final Pattern QUESTION_PATTERN = Pattern.compile("/questions/\\d+(/[A-Za-z0-9-]+)?");
    private final StackoverflowClient stackoverflowClient;

    @Override
    public boolean supports(Link link) {
        return supportHostname(link) && supportsPath(link);
    }

    private boolean supportHostname(Link link) {
        return link.url().getHost().equals(ResourceUpdaterConstants.STACKOVERFLOW_HOST) && link.type()
            .equals(Type.STACKOVERFLOW);
    }

    private boolean supportsPath(Link link) {
        String path = link.url().getPath();
        return QUESTION_PATTERN.matcher(path).matches();
    }

    @Override
    public Optional<UpdateInfo> updateResource(Link link) {
        String[] splitPath = link.url().getPath().split("/");
        Long questionId = Long.parseLong(splitPath[2]);

        StackoverflowQuestion stackoverflowQuestion;
        try {
            stackoverflowQuestion = stackoverflowClient.getQuestion(questionId);
        } catch (WebClientException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        StackoverflowQuestion.Item questionItem = stackoverflowQuestion.items().getFirst();
        if (questionItem.lastActivityDate().equals(link.updatedAt())) {
            return Optional.empty();
        }

        log.info("Checking updates at: " + link.url());

        StackoverflowAnswers stackoverflowAnswers;
        StackoverflowComments stackoverflowComments;
        try {
            stackoverflowAnswers = stackoverflowClient.getAnswers(questionId);
            stackoverflowComments = stackoverflowClient.getComments(questionId);
        } catch (WebClientException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        String description = ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(questionItem.title())
                             + generateAnswersMessage(link, stackoverflowAnswers, questionItem.lastActivityDate())
                             + generateCommentsMessage(link, stackoverflowComments, questionItem.lastActivityDate());

        return Optional.of(
            createUpdateInfo(
                link,
                questionItem.lastActivityDate(),
                stackoverflowAnswers.items().size(),
                stackoverflowComments.items().size(),
                description
            ));
    }

    private String generateAnswersMessage(
        Link link,
        StackoverflowAnswers answers,
        OffsetDateTime updatedAt
    ) {
        int answerCount = link.answerCount() == null ? 0 : link.answerCount();

        if (answers.items().size() == answerCount) {
            return ResourceUpdaterConstants.EMPTY_STRING;
        }

        if (answers.items().size() > answerCount) {
            StringBuilder stringBuilder = new StringBuilder();
            answers.items().stream()
                .filter(item -> item.creationDate().isAfter(updatedAt))
                .forEach(item -> stringBuilder.append(ResourceUpdaterConstants.STACKOVERFLOW_NEW_ANSWER
                    .formatted(item.owner().name())));

            return stringBuilder.toString();
        }

        return ResourceUpdaterConstants.STACKOVERFLOW_ANSWER_DELETED;
    }

    private String generateCommentsMessage(
        Link link,
        StackoverflowComments comments,
        OffsetDateTime updatedAt
    ) {
        int commentCount = link.commentCount() == null ? 0 : link.commentCount();

        if (comments.items().size() == commentCount) {
            return ResourceUpdaterConstants.EMPTY_STRING;
        }

        if (comments.items().size() > commentCount) {
            StringBuilder stringBuilder = new StringBuilder();
            comments.items().stream()
                .filter(item -> item.creationDate().isAfter(updatedAt))
                .forEach(item -> stringBuilder.append(ResourceUpdaterConstants.STACKOVERFLOW_NEW_COMMENT
                    .formatted(item.owner().name())));

            return stringBuilder.toString();
        }

        return ResourceUpdaterConstants.STACKOVERFLOW_COMMENT_DELETED;
    }

    private UpdateInfo createUpdateInfo(
        Link link,
        OffsetDateTime updatedAt,
        Integer answerCount,
        Integer commentCount,
        String description
    ) {
        Link updatedLink = new Link(
            link.id(),
            link.url(),
            updatedAt,
            link.checkedAt(),
            link.type(),
            answerCount,
            commentCount,
            link.pullRequestCount(),
            link.commitCount()
        );
        return new UpdateInfo(updatedLink, description);
    }
}
