package edu.java.scrapper.domain.links.jpa;

import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.tgchat.jpa.Chat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private URI url;

    @ColumnDefault("current_timestamp")
    @Column(nullable = false, insertable = false)
    private OffsetDateTime updatedAt;

    @ColumnDefault("current_timestamp")
    @Column(nullable = false, insertable = false)
    private OffsetDateTime checkedAt;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Type type;

    private Integer answerCount;

    private Integer commentCount;

    private Integer pullRequestCount;

    private Integer commitCount;

    @ManyToMany(mappedBy = "links")
    Set<Chat> chats = new HashSet<>();
}
