package ru.rinattzak.service.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.rinattzak.model.MessageTextProcessorResult;
import ru.rinattzak.service.process.enums.CmdRegistry;

@Service("messageTextProcessorGeneral")
@Slf4j
public class MessageTextProcessorGeneral implements MessageTextProcessor {
    private final ApplicationContext applicationContext;
    private final MessageTextProcessor messageTextProcessor;

    public MessageTextProcessorGeneral(ApplicationContext applicationContext,
                                       @Qualifier("messageTextProcessorRate") MessageTextProcessor messageTextProcessor) {
        this.applicationContext = applicationContext;
        this.messageTextProcessor = messageTextProcessor;
    }

    @Override
    public MessageTextProcessorResult process(String message) {
        for (CmdRegistry cmd : CmdRegistry.values()) {
            if (cmd.getCmd().equals(message)) {
                MessageTextProcessor handler = applicationContext.getBean(cmd.getHandlerName(), MessageTextProcessor.class);
                return handler.process(message);
            }
        }
        return messageTextProcessor.process(message);
    }
}
