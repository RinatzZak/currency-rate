package ru.rinattzak.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rinattzak.exception.HttpClientException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class HttpClientJdk implements HttpClient {
    @Override
    public String performRequest(String url, String params) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .build();
        return doRequest(url, request);
    }

    @Override
    public String performRequest(String url) {
        log.info("http request, url{}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        return doRequest(url, request);
    }

    @Override
    public String doRequest(String url, HttpRequest request) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Http request error, url{}", url);
            throw new HttpClientException(e.getMessage());
        }
    }
}
