package ru.rinattzak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;
import ru.rinattzak.config.CbrConfig;
import ru.rinattzak.exception.CurrencyNotFoundException;
import ru.rinattzak.model.CachedCurrencyRates;
import ru.rinattzak.model.CurrencyRate;
import ru.rinattzak.parser.CurrencyRateParser;
import ru.rinattzak.requester.CbrRequester;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateService {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CbrRequester cbrRequester;
    private final CurrencyRateParser currencyRateParser;
    private final CbrConfig cbrConfig;
    private final Cache<LocalDate, CachedCurrencyRates> currencyRatesCache;

    public CurrencyRate getCurrencyRate(String currency, LocalDate date) {
        log.info("get currency rate, currency{}, date{}", currency, date);
        List<CurrencyRate> currencyRateList;

        var cachedCurrencyRates = currencyRatesCache.get(date);
        if (cachedCurrencyRates == null) {
            String urlWithParam = String.format("%s?date_req=%s", cbrConfig.getUrl(), DATE_TIME_FORMATTER.format(date));
            String rateAsXml = cbrRequester.getRatesAsXml(urlWithParam);
            currencyRateList = currencyRateParser.parse(rateAsXml);
            currencyRatesCache.put(date, new CachedCurrencyRates(currencyRateList));
        } else {
            currencyRateList = cachedCurrencyRates.getCurrencyRateList();
        }
        return currencyRateList.stream()
                .filter(currencyRate -> currencyRate.equals(currencyRate.getCharCode()))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Not found currency. Currency: " + currency + ", date: " + date));
    }
}
