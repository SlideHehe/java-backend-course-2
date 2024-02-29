package edu.java.scrapper.api.exception;

public class LinkAlreadyExistsException extends RuntimeException {
    public LinkAlreadyExistsException(String message) {
        super(message);
    }
}
