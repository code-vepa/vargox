package com.codevepa.vargox.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component 
public class FinnhubClient {
    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.api.url}")
    private String apiUrl;

    public FinnhubClient() {
        this.restClient = RestClient.create();
    }

    public FinnhubQuoteResponse getQuote(String symbol) {
        return restClient.get()
                .uri(apiUrl + "?symbol=" + symbol + "&token=" + apiKey)
                .retrieve()
                .body(FinnhubQuoteResponse.class);
    }
}
