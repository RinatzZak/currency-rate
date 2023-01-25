package ru.rinattzak.clients;

import ru.rinattzak.model.GetUpdateRequest;
import ru.rinattzak.model.GetUpdateResponse;
import ru.rinattzak.model.SendMessageRequest;

public interface TelegramClient {
    void sendMessage(SendMessageRequest request);
    GetUpdateResponse getUpdates(GetUpdateRequest request);
}
