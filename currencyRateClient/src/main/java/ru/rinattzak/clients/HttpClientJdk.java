package ru.rinattzak.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.rinattzak.exception.HttpClientException;

@Service
@Slf4j
public class HttpClientJdk implements HttpClient{

    private final WebClient.Builder webBuilder;

    public HttpClientJdk(WebClient.Builder webBuilder) {
        this.webBuilder = webBuilder;
    }

    @Override
    public Mono<String> performRequest(String url) {
        log.info("request, url: {}", url);
        var client = webBuilder.baseUrl(url).build();
        try {
            return client.get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> log.info("Error request, url: {}", url, e))
                    .doOnNext(val -> log.info("val: {}", val));
        } catch (Exception e) {
            throw new HttpClientException(e.getMessage());
        }
    }
}
