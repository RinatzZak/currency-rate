package ru.rinattzak.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.rinattzak.config.AppConfig;
import ru.rinattzak.config.CbrConfig;
import ru.rinattzak.config.JsonConfig;
import ru.rinattzak.parser.CurrencyRateParserXml;
import ru.rinattzak.requester.CbrRequester;
import ru.rinattzak.service.CurrencyRateService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({AppConfig.class, JsonConfig.class, CurrencyRateService.class, CurrencyRateParserXml.class})
public class CurrencyRateControllerTest {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    CbrConfig cbrConfig;

    @MockBean
    CbrRequester cbrRequester;

    @Test
    @DirtiesContext
    void getCurrencyRateTest() throws Exception {
        var date = "11-01-2023";
        var currency = "EUR";
        prepareCbrRequesterMock(date);

        var result = webTestClient
                .get().uri(String.format("/api/v1/currencyRate/%s/%s", currency, date))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockLast();

        assertThat(result).isEqualTo("{\"numCode\":\"978\",\"charCode\":\"EUR\",\"nominal\":\"1\",\"name\":\"Евро\",\"value\":\"74,5438\"}");
    }

    @Test
    @DirtiesContext
    void cacheUseTest() throws IOException, URISyntaxException {
        prepareCbrRequesterMock(null);

        var currency = "EUR";
        var date = "12-01-2023";

        webTestClient.get().uri(String.format("/api/v1/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();
        webTestClient.get().uri(String.format("/api/v1/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        currency = "USD";
        webTestClient.get().uri(String.format("/api/v1/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        date = "05-11-2021";
        webTestClient.get().uri(String.format("/api/v1/currencyRate/%s/%s", currency, date)).exchange().expectStatus().isOk();

        verify(cbrRequester, times(2)).getRatesAsXml(any());
    }

    private void prepareCbrRequesterMock(String date) throws IOException, URISyntaxException {
        var uri = ClassLoader.getSystemResource("XML_daily.xml").toURI();
        var ratesXml = Files.readString(Paths.get(uri), Charset.forName("Windows-1251"));

        if (date == null) {
            when(cbrRequester.getRatesAsXml(any())).thenReturn(ratesXml);
        } else {
            var dateParam = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            var cbrUrl = String.format("%s?date_req=%s", cbrConfig.getUrl(), DATE_TIME_FORMATTER.format(dateParam));
            when(cbrRequester.getRatesAsXml(cbrUrl)).thenReturn(ratesXml);
        }
    }
}
