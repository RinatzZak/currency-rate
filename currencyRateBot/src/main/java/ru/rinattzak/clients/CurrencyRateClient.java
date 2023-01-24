package ru.rinattzak.clients;

import ru.rinattzak.model.CurrencyRate;

import java.time.LocalDate;

public interface CurrencyRateClient {
    CurrencyRate getCurrencyRate(String rateType, String currency, LocalDate date);
}
