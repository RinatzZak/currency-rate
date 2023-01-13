package ru.rinattzak.clients;

import reactor.core.publisher.Mono;
import ru.rinattzak.model.CurrencyRate;

import java.time.LocalDate;

public interface RateClient {
    Mono<CurrencyRate> getCurrencyRate(String currency, LocalDate date);
}
