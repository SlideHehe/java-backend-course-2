package edu.java.bot.telegram.service.linkvalidator;

import java.net.URI;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowLinkValidator implements LinkValidator {
    private static final Pattern QUESTION_PATTERN = Pattern.compile("/questions/\\d+(/[A-Za-z0-9-]+)?");

    @Override
    public String hostname() {
        return "stackoverflow.com";
    }

    @Override
    public boolean supports(@NotNull URI url) {
        String path = url.getPath();
        String host = url.getHost();
        return QUESTION_PATTERN.matcher(path).find() && host.equals(hostname());
    }
}
