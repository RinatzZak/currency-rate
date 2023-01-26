package ru.rinattzak.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rinattzak.clients.TelegramClient;
import ru.rinattzak.exception.TelegramException;
import ru.rinattzak.service.LastUpdateIdKeeper;
import ru.rinattzak.service.TelegramService;
import ru.rinattzak.service.impl.TelegramServiceImpl;
import ru.rinattzak.service.process.MessageTextProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(CurrencyRateClientConfig.class)
public class AppConfig {

    public static final String TG_TOKEN_ENV_NAME = "TG_TOKEN";
    public static final String TOKEN_FILE = "TOKEN_FILE";

    @Bean
    public TelegramClientConfig telegramClientConfig(@Value("${app.telegram.url}") String url,
                                                     @Value("${app.telegram.refresh-rate-ms}") int refreshRateMs) {
        String token = System.getProperty(TG_TOKEN_ENV_NAME);
        if (token == null) {
            token = System.getenv(TG_TOKEN_ENV_NAME);
        }
        if (token == null) {
            String tokenFile = System.getenv(TOKEN_FILE);
            token = readFile(tokenFile);
        }
        if (token == null) {
            log.error("token not found");
            throw new TelegramException("Token not found");
        }
        return new TelegramClientConfig(url, token, refreshRateMs);
    }

    @Bean
    public TelegramImporterScheduled telegramImporterScheduled(TelegramClient telegramClient, TelegramClientConfig telegramClientConfig,
                                                               @Qualifier("messageTextProcessorGeneral") MessageTextProcessor messageTextProcessor,
                                                               LastUpdateIdKeeper lastUpdateIdKeeper) {
        var telegramService = new TelegramServiceImpl(telegramClient, messageTextProcessor, lastUpdateIdKeeper);
        return new TelegramImporterScheduled(telegramService, telegramClientConfig);
    }

    public static class TelegramImporterScheduled {
        public TelegramImporterScheduled(TelegramService telegramService, TelegramClientConfig telegramClientConfig) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(telegramService::getUpdates, 1000, telegramClientConfig.getRefreshRateMs(), TimeUnit.MILLISECONDS);
        }
    }

    private String readFile(String tokenFile) {
        try {
            if (tokenFile != null) {
                return Files.readString(Path.of(tokenFile));
            }
            return null;
        } catch (IOException e) {
            throw new TelegramException("Can't read file: " + tokenFile);
        }
    }
}
