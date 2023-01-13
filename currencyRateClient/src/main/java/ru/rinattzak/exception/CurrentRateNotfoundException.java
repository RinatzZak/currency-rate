package ru.rinattzak.exception;

public class CurrentRateNotfoundException extends RuntimeException {
    public CurrentRateNotfoundException(String message) {
        super(message);
    }
}
