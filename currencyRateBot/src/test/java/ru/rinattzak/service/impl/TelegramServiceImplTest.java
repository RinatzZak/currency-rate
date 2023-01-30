package ru.rinattzak.service.impl;

import org.junit.jupiter.api.Test;
import ru.rinattzak.clients.TelegramClient;
import ru.rinattzak.model.GetUpdateRequest;
import ru.rinattzak.model.GetUpdateResponse;
import ru.rinattzak.model.MessageTextProcessorResult;
import ru.rinattzak.model.SendMessageRequest;
import ru.rinattzak.service.process.MessageTextProcessor;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

class TelegramServiceImplTest {

    @Test
    void getUpdates() {
        TelegramClient telegramClient = mock(TelegramClient.class);
        String text = "text";
        MessageTextProcessor messageTextProcessor = mock(MessageTextProcessor.class);
        String reply = "ok";
        when(messageTextProcessor.process(text)).thenReturn(new MessageTextProcessorResult(reply, null));

        LastUpdateIdKeeperImpl lastUpdateIdKeeper = spy(LastUpdateIdKeeperImpl.class);
        TelegramServiceImpl telegramService = new TelegramServiceImpl(telegramClient, messageTextProcessor,
                lastUpdateIdKeeper);

        GetUpdateRequest request = new GetUpdateRequest(0);
        GetUpdateResponse response = makeGetUpdateResponse(1, text);
        when(telegramClient.getUpdates(request)).thenReturn(response);

        telegramService.getUpdates();

        SendMessageRequest sendMessageRequest1 = new SendMessageRequest(
                response.getResult().get(0).getMessage().getChat().getId(),
                reply, response.getResult().get(0).getMessage().getMessageId());

        SendMessageRequest sendMessageRequest2 = new SendMessageRequest(
                response.getResult().get(1).getMessage().getChat().getId(),
                reply, response.getResult().get(1).getMessage().getMessageId());

        verify(telegramClient).sendMessage(sendMessageRequest1);
        verify(telegramClient).sendMessage(sendMessageRequest2);
        verify(lastUpdateIdKeeper).set(response.getResult().get(1).getUpdateId() + 1);

        GetUpdateRequest request1 = new GetUpdateRequest(response.getResult().get(1).getUpdateId() + 1);
        GetUpdateResponse response1 = makeGetUpdateResponse(2, text);
        when(telegramClient.getUpdates(request1)).thenReturn(response1);

        telegramService.getUpdates();

        SendMessageRequest sendMessageRequest3 = new SendMessageRequest(
                response1.getResult().get(0).getMessage().getChat().getId(), reply,
                response1.getResult().get(0).getMessage().getMessageId());

        SendMessageRequest sendMessageRequest4 = new SendMessageRequest(
                response1.getResult().get(1).getMessage().getChat().getId(), reply,
                response1.getResult().get(1).getMessage().getMessageId());

        verify(telegramClient).sendMessage(sendMessageRequest3);
        verify(telegramClient).sendMessage(sendMessageRequest4);
        verify(lastUpdateIdKeeper).set(response1.getResult().get(1).getUpdateId());
    }

    private GetUpdateResponse makeGetUpdateResponse(long updateId, String text) {
        var from = new GetUpdateResponse.From(506L, false, "Ivan", "Petrov", "en");
        var chat = new GetUpdateResponse.Chat(506L, "Ivan", "Petrov", "private");
        var random = new Random();
        var message1 = new GetUpdateResponse.Message(random.nextLong(), from, chat, 1631970287, text);
        var message2 = new GetUpdateResponse.Message(random.nextLong(), from, chat, 1631970287, text);

        return new GetUpdateResponse(true, List.of(new GetUpdateResponse.Response(updateId, message1),
                new GetUpdateResponse.Response(updateId + 1, message2)));
    }
}