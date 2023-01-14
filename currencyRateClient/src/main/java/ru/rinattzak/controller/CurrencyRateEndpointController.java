package ru.rinattzak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rinattzak.model.CurrencyRate;
import ru.rinattzak.model.RateType;
import ru.rinattzak.service.CurrencyRateEndpointService;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "${app.rest.api.prefix}/v1")
public class CurrencyRateEndpointController {

    public final CurrencyRateEndpointService currencyRateEndpointService;

    @GetMapping("/currencyRate/{type}/{currency}/{date}")
    public Mono<CurrencyRate> getCurrencyRate(@PathVariable("type") RateType rateType,
                                              @PathVariable("currency") String currency,
                                              @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable("date") LocalDate date) {
        log.info("getCurrencyRate controller, rateType: {}, currency: {}. date: {}",
                rateType, currency, date);

        return currencyRateEndpointService.getCurrencyRate(rateType, currency, date);
    }
}
