package ru.rinattzak.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rinattzak.config.CurrencyRateClientConfig;
import ru.rinattzak.exception.CurrencyRateClientException;
import ru.rinattzak.exception.HttpClientException;
import ru.rinattzak.model.CurrencyRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateClientImpl implements CurrencyRateClient {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CurrencyRateClientConfig currencyRateClientConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public CurrencyRate getCurrencyRate(String rateType, String currency, LocalDate date) {
        log.info("currencyRate, rateType:{}, currency:{}, date:{}", rateType, currency, date);
        String urlWithParams = String.format("%s/%s/%s/%s", currencyRateClientConfig.getUrl(),
                rateType, currency, DATE_TIME_FORMATTER.format(date));

        try {
            String response = httpClient.performRequest(urlWithParams);
            return objectMapper.readValue(response, CurrencyRate.class);
        } catch (HttpClientException e) {
            throw new CurrencyRateClientException(e.getMessage());
        } catch (Exception e) {
            log.error("getCurrencyRate error, currency:{}, date:{}", currency, date, e);
            throw new CurrencyRateClientException("getCurrencyRate error, currency: " + currency
                    + ", date: " + date);
        }
    }
}
