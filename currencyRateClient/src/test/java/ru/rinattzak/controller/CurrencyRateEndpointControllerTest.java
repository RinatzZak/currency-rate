package ru.rinattzak.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.rinattzak.clients.HttpClient;
import ru.rinattzak.config.CbrRateClientConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyRateEndpointControllerTest {
        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        CbrRateClientConfig cbrRateClientConfig;

        @MockBean
        HttpClient httpClient;

        @Test
        void getCurrencyRateTest()  {
            var type = "CBR";
            var currency = "EUR";
            var date = "11-01-2021";

            var url = String.format("%s/%s/%s", cbrRateClientConfig.getUrl(), currency, date);
            when(httpClient.performRequest(url))
                    .thenReturn(Mono.just("{\"numCode\":\"978\",\"charCode\":\"EUR\",\"nominal\":\"1\",\"name\":\"Евро\",\"value\":\"74,5438\"}"));
            //when

            var result = webTestClient
                    .get()
                    .uri(String.format("/api/v1/currencyRate/%s/%s/%s", type, currency, date))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(String.class)
                    .getResponseBody()
                    .blockLast();

            assertThat(result).isEqualTo("{\"charCode\":\"EUR\",\"nominal\":\"1\",\"value\":\"74,5438\"}");
        }
}
