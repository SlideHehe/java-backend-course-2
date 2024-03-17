package edu.java.scrapper.api.links;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    List<Link> findAll();

    List<Link> findCheckedMoreThanSomeSecondsAgo(Long secondsAgo);

    Optional<Link> findById(Long id);

    Optional<Link> findByUrl(URI uri);

    Link add(URI url, Type type);

    Link updateUpdatedTimestamp(Long id, OffsetDateTime updatedAt);

    List<Link> updateCheckedTimestamp(List<Long> ids);

    Link updateStackoverflowLink(Long id, Integer answerCount, Integer commentCount);

    Link updateGithubLink(Long id, Integer pullRequestCount, Integer commitCount);

    Link remove(Long id);

    List<Link> findAllByChatId(Long chatId);
}
