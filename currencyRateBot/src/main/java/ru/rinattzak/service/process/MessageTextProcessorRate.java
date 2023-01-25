package ru.rinattzak.service.process;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rinattzak.clients.CurrencyRateClient;
import ru.rinattzak.model.CurrencyRate;
import ru.rinattzak.model.MessageTextProcessorResult;
import ru.rinattzak.service.DateTimeProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ru.rinattzak.service.process.enums.Messages.DATA_FORMAT_MSG;
import static ru.rinattzak.service.process.enums.Messages.EXPECTED_FORMAT_MSG;

@Service("messageTextProcessorRate")
@Slf4j
@AllArgsConstructor
public class MessageTextProcessorRate implements MessageTextProcessor {
    private static final String CBR_RATE_CONST = "CBR";
    private static final String DATE_FORMAT_ZERO = "dd-MM-yyyy";
    private static final String DATE_FORMAT = "d-MM-yyyy";
    private static final DateTimeFormatter DATE_FORMATTER_ZERO = DateTimeFormatter.ofPattern(DATE_FORMAT_ZERO);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CurrencyRateClient currencyRateClient;
    private final DateTimeProvider dateTimeProvider;

    @Override
    public MessageTextProcessorResult process(String message) {
        log.info("message: {}", message);

        String[] textParts = message.split(" ");

        if (textParts.length < 1 || textParts.length > 3) {
            return new MessageTextProcessorResult(null, EXPECTED_FORMAT_MSG.getText());
        }

        String rateType = null;
        String currency = null;
        String dateAsString = null;
        LocalDate localDate = null;

        if (textParts.length == 3) {
            rateType = textParts[0];
            currency = textParts[1];
            dateAsString = textParts[2];
        }

        if (textParts.length == 2) {
            rateType = CBR_RATE_CONST;
            currency = textParts[0];
            dateAsString = textParts[1];
        }

        if (textParts.length == 1) {
            rateType = CBR_RATE_CONST;
            currency = textParts[0];
            localDate = dateTimeProvider.get().toLocalDate();
        }

        if (textParts.length == 3 || textParts.length == 2) {
            try {
                localDate = parseDate(dateAsString);
            } catch (Exception e) {
                log.error("Error parse, dateAsString: {}", dateAsString, e);
                return new MessageTextProcessorResult(null, DATA_FORMAT_MSG.getText());
            }
        }

        if (rateType == null || currency == null) {
            log.error("rateType {} == null || currency {} == null", rateType, currency);
            throw new IllegalArgumentException("rateType: " + rateType + " or currency: " + currency + " is null");
        }

        CurrencyRate rate = currencyRateClient.getCurrencyRate(rateType, currency, localDate);
        return new MessageTextProcessorResult(rate.getValue(), null);
    }

    private LocalDate parseDate(String dateAsString) {
        try {
            return LocalDate.parse(dateAsString, DATE_FORMATTER_ZERO);
        } catch (Exception e) {
            return LocalDate.parse(dateAsString, DATE_FORMATTER);
        }
    }
}
