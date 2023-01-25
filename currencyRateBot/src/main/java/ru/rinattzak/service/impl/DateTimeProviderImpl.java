package ru.rinattzak.service.impl;

import org.springframework.stereotype.Service;
import ru.rinattzak.service.DateTimeProvider;

import java.time.LocalDateTime;

@Service
public class DateTimeProviderImpl implements DateTimeProvider {
    @Override
    public LocalDateTime get() {
        return LocalDateTime.now();
    }
}
