package ru.rinattzak.clients;

import java.net.http.HttpRequest;

public interface HttpClient {
    String performRequest(String url, String params);
    String performRequest(String url);
    String doRequest(String url, HttpRequest request);
}
