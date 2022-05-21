package ru.yandex.practicum.servers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KvTaskClient {
    private final String apiKey;
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    private final HttpClient client = HttpClient.newHttpClient();
    private final URI url;

    public KvTaskClient(URI url) {
        this.url = url;
        apiKey = registerApiKey();
    }


    public URI getUrl() {
        return url;
    }

    public void put(String key, String json) throws IOException, InterruptedException {

        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url + "/save/" + key + "?API_KEY=" + apiKey)).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) {
        HttpRequest request = requestBuilder.GET().uri(URI.create(url + "/load/" + key + "?API_KEY=" + apiKey)).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }


    private String registerApiKey() {
        HttpRequest request = requestBuilder.GET().uri(URI.create(url + "/register")).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }
}


