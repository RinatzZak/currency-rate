package ru.rinattzak.service.process;

import org.springframework.stereotype.Service;
import ru.rinattzak.model.MessageTextProcessorResult;

import static ru.rinattzak.service.process.enums.Messages.EXPECTED_FORMAT_MSG;

@Service("messageTextProcessorStart")
public class MessageTextProcessorStart implements MessageTextProcessor{
    @Override
    public MessageTextProcessorResult process(String message) {
        return new MessageTextProcessorResult(EXPECTED_FORMAT_MSG.getText(), null);
    }
}
