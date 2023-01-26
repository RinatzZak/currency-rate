package ru.rinattzak.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.rinattzak.clients.TelegramClient;
import ru.rinattzak.model.GetUpdateRequest;
import ru.rinattzak.model.GetUpdateResponse;
import ru.rinattzak.model.MessageTextProcessorResult;
import ru.rinattzak.model.SendMessageRequest;
import ru.rinattzak.service.LastUpdateIdKeeper;
import ru.rinattzak.service.TelegramService;
import ru.rinattzak.service.process.MessageTextProcessor;

@Slf4j
public class TelegramServiceImpl implements TelegramService {
    private final TelegramClient telegramClient;
    private final MessageTextProcessor messageTextProcessor;
    private final LastUpdateIdKeeper lastUpdateIdKeeper;

    public TelegramServiceImpl(TelegramClient telegramClient, MessageTextProcessor messageTextProcessor,
                               LastUpdateIdKeeper lastUpdateIdKeeper) {
        this.telegramClient = telegramClient;
        this.messageTextProcessor = messageTextProcessor;
        this.lastUpdateIdKeeper = lastUpdateIdKeeper;
    }

    @Override
    public void getUpdates() {
        try {
            log.info("getUpdates begin");
            long offset = lastUpdateIdKeeper.get();
            GetUpdateRequest request = new GetUpdateRequest(offset);
            GetUpdateResponse response = telegramClient.getUpdates(request);
            long lastUpdateId = processResponse(response);
            lastUpdateId = lastUpdateId == 0 ? offset : lastUpdateId + 1;
            lastUpdateIdKeeper.set(lastUpdateId);
            log.info("getUpdates finish, id: {}", lastUpdateId);
        } catch (Exception e) {
            log.error("unhandled ex", e);
        }
    }

    private long processResponse(GetUpdateResponse response) {
        log.info("response get result size: {}", response.getResult().size());
        long lastUpdateId = 0;
        for (var responseMessage : response.getResult()) {
            lastUpdateId = Math.max(lastUpdateId, responseMessage.getUpdateId());
            processMessage(responseMessage.getMessage());
        }
        log.info("lastUpdateId-{}", lastUpdateId);
        return lastUpdateId;
    }

    private void processMessage(GetUpdateResponse.Message message) {
        log.info("message:{}", message);

        long chatId = message.getChat().getId();
        long messageId = message.getMessageId();

        MessageTextProcessorResult result = messageTextProcessor.process(message.getText());
        String replay = result.getFailReply() == null ? result.getOkReply() : result.getFailReply();
        SendMessageRequest request = new SendMessageRequest(chatId, replay, messageId);
        telegramClient.sendMessage(request);
    }
}
