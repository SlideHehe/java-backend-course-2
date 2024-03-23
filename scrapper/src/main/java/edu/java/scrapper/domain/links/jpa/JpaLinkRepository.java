package edu.java.scrapper.domain.links.jpa;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface JpaLinkRepository extends CrudRepository<Link, Long> {
    void deleteAllByChatsEmpty();

    @SuppressWarnings("MethodName")
    List<Link> findAllByChats_Id(Long id);

    Optional<Link> findByUrl(URI url);

    @SuppressWarnings("MethodName")
    boolean existsByChats_IdAndUrl(Long id, URI url);

    @Query(value = "select * from link where extract(epoch from (current_timestamp - link.checked_at)) > :secondsAgo",
           nativeQuery = true)
    List<Link> findByCheckedMoreThanSomeSecondsAgo(@Param("secondsAgo") Long secondsAgo);

    @Modifying
    @Query(value = "update link set checked_at = default where link.id in :ids", nativeQuery = true)
    void updateCheckedAtByIdIn(@Param("ids") Collection<Long> ids);

    @Modifying
    @Query(value = "update Link l set l.updatedAt = :updatedAt where l.id = :id")
    void updateUpdatedAtById(@Param("updatedAt") OffsetDateTime updatedAt, @Param("id") Long id);

    @Modifying
    @Query(value = "update Link l set "
                   + "l.answerCount = :answerCount, l.commentCount = :commentCount, "
                   + "l.pullRequestCount = :pullRequestCount, l.commitCount = :commitCount "
                   + "where l.id = :id")
    void updateCountersById(
        @Param("answerCount") Integer answerCount,
        @Param("commentCount") Integer commentCount,
        @Param("pullRequestCount") Integer pullRequestCount,
        @Param("commitCount") Integer commitCount,
        @Param("id") Long id
    );

}
