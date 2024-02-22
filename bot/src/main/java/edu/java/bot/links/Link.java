package edu.java.bot.links;

import java.util.Objects;
import lombok.Getter;

@Getter
public class Link {
    private final String url;
    private final String hostname;

    public Link(String hostname, String url) {
        this.hostname = hostname;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(url, link.url) && Objects.equals(hostname, link.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, hostname);
    }
}
