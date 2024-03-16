package edu.java.bot.api.exception;

public class UpdateAlreadyExistsException extends RuntimeException {
    public UpdateAlreadyExistsException(String message) {
        super(message);
    }
}
