package edu.java.bot.telegram.service.linkvalidator;

import java.net.URI;
import org.jetbrains.annotations.NotNull;

public interface LinkValidator {
    String hostname();

    boolean supports(@NotNull URI url);
}
