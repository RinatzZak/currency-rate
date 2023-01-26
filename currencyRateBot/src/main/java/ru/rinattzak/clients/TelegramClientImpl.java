package ru.rinattzak.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rinattzak.config.TelegramClientConfig;
import ru.rinattzak.exception.TelegramException;
import ru.rinattzak.model.GetUpdateRequest;
import ru.rinattzak.model.GetUpdateResponse;
import ru.rinattzak.model.SendMessageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramClientImpl implements TelegramClient {

    private final HttpClientJdk httpClientJdk;
    private final ObjectMapper objectMapper;
    private final TelegramClientConfig telegramClientConfig;

    @Override
    public void sendMessage(SendMessageRequest request) {
        try {
            String params = objectMapper.writeValueAsString(request);
            log.info("params{}", params);

            String responseAsString = httpClientJdk.performRequest(makeUrl("sendMessage"), params);
            log.info("responseAsString {}", responseAsString);
        } catch (JsonProcessingException e) {
            log.error("request{}", request);
            throw new TelegramException(e.getMessage());
        }
    }

    @Override
    public GetUpdateResponse getUpdates(GetUpdateRequest request) {
        try {
            String params = objectMapper.writeValueAsString(request);
            log.info("params{}", params);

            String updatesAsString = httpClientJdk.performRequest(makeUrl("getUpdates"), params);
            log.info("updatesAsString {}", updatesAsString);
            GetUpdateResponse updates = objectMapper.readValue(updatesAsString, GetUpdateResponse.class);
            log.info("updates - {}", updates);

            return updates;
        } catch (JsonProcessingException e) {
            log.error("request{}", request);
            throw new TelegramException(e.getMessage());
        }
    }

    private String makeUrl(String request) {
        return String.format("%s/bot%s/%s", telegramClientConfig.getUrl(),
                telegramClientConfig.getToken(), request);
    }
}
