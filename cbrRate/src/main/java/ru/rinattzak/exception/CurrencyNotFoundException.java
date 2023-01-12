package ru.rinattzak.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(Throwable cause) {
        super(cause);
    }
}
