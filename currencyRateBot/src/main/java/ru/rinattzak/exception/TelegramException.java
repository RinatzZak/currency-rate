package ru.rinattzak.exception;

public class TelegramException extends RuntimeException {
    public TelegramException(String message) {
        super(message);
    }
}
