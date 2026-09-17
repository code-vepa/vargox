package com.codevepa.vargox.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinnhubQuoteResponse {
    
    @JsonProperty("c")
    private double currentPrice;

    @JsonProperty("pc")
    private double previousClose;

    @JsonProperty("h")
    private double high;

    @JsonProperty("l")
    private double low;

    @JsonProperty("o")
    private double open;

    @JsonProperty("t")
    private long timestamp;

    public double getCurrentPrice() { return currentPrice; }
    public double getPreviousClose() { return previousClose; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getOpen() { return open; }
    public long getTimestamp() { return timestamp; }
}
