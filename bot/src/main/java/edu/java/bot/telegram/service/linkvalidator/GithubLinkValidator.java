package edu.java.bot.telegram.service.linkvalidator;

import java.net.URI;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class GithubLinkValidator implements LinkValidator {
    private static final Pattern REPOSITORY_PATTERN = Pattern.compile("/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+");

    @Override
    public String hostname() {
        return "github.com";
    }

    @Override
    public boolean supports(@NotNull URI url) {
        String path = url.getPath();
        String host = url.getHost();
        return REPOSITORY_PATTERN.matcher(path).find() && host.equals(hostname());
    }
}
