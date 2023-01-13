package ru.rinattzak.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.rinattzak.config.CbrRateClientConfig;
import ru.rinattzak.exception.HttpClientException;
import ru.rinattzak.exception.RateClientException;
import ru.rinattzak.model.CurrencyRate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("cbr")
@Slf4j
@RequiredArgsConstructor
public class CbrRateClient implements RateClient{
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CbrRateClientConfig cbrRateClientConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<CurrencyRate> getCurrencyRate(String currency, LocalDate date) {
        log.info("getCurrencyRate currency: {}, date: {}", currency, date);
        var urlWithParams = String.format("%s/%s/%s", cbrRateClientConfig.getUrl(), currency,
                DATE_TIME_FORMATTER.format(date));

        try {
            return httpClient.performRequest(urlWithParams)
                    .map(this::parse);
        } catch (HttpClientException e) {
            throw new RateClientException("Error Cbr Client: " + e.getMessage());
        } catch (Exception e) {
            log.error("getCurrencyRate Error, currency: {}, date: {}", currency, date);
            throw new RateClientException("getCurrencyRate error, currency: " + currency + ", date: " + date);
        }
    }

    private CurrencyRate parse(String rateAsString) {
        try {
            return objectMapper.readValue(rateAsString, CurrencyRate.class);
        } catch (Exception e) {
            throw new RateClientException("Error parse - " + rateAsString);
        }
    }
}
