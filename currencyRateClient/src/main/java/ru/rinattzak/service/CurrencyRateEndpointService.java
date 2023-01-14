package ru.rinattzak.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.rinattzak.clients.RateClient;
import ru.rinattzak.model.CurrencyRate;
import ru.rinattzak.model.RateType;

import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
public class CurrencyRateEndpointService {
    private final Map<String, RateClient> clientMap;

    public CurrencyRateEndpointService(Map<String, RateClient> clientMap) {
        this.clientMap = clientMap;
    }

    public Mono<CurrencyRate> getCurrencyRate(RateType rateType, String currency, LocalDate date) {
        log.info("getCurrencyRate service, rateType: {}, currency: {}, date: {}", rateType, currency, date);
        var client = clientMap.get(rateType.getServiceName());
        return client.getCurrencyRate(currency, date);
    }
}
