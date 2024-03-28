package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.api.links.Link;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import edu.java.scrapper.scheduler.UpdateInfo;
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
        return link.url().getHost().equals(ResourceUpdaterConstants.STACKOVERFLOW_HOST);
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

        UpdateInfo updateInfo = new UpdateInfo(
            link.id(),
            link.url(),
            ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE,
            questionItem.lastActivityDate()
        );

        return Optional.of(updateInfo);
    }
}
