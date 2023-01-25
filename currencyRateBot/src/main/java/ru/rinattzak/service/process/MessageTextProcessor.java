package ru.rinattzak.service.process;

import ru.rinattzak.model.MessageTextProcessorResult;

public interface MessageTextProcessor {
    MessageTextProcessorResult process(String message);
}
