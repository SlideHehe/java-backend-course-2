package edu.java.bot.telegram.link;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class User {
    private final Long id;
    private final Set<Link> links;

    public User(@NotNull Long id) {
        this.id = id;
        links = new HashSet<>();
    }

    public boolean addLink(@NotNull Link link) {
        return links.add(link);
    }

    public void removeLink(@NotNull Link link) {
        links.remove(link);
    }
}
