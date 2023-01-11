package ru.rinattzak.parser;

import ru.rinattzak.model.CurrencyRate;

import java.util.List;

public interface CurrencyRateParser {
    List<CurrencyRate> parse(String rateAsString);
}
