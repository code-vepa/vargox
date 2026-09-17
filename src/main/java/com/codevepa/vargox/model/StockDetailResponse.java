package com.codevepa.vargox.model;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;

public class StockDetailResponse {

    private Long id;
    private String companyName;
    private String symbol;
    private String exchange;
    private String sector;
    private Double currentPrice;
    private Double previousPrice;
    private Long volume;


    public StockDetailResponse(Stock stock, StockPrice price) {
        this.id = stock.getId();
        this.symbol = stock.getSymbol();
        this.companyName = stock.getCompanyName();
        this.exchange = stock.getExchange();
        this.sector = stock.getSector();

        if (price != null) {
            this.currentPrice = price.getCurrentPrice();
            this.previousPrice = price.getPreviousPrice();
            this.volume = price.getVolume();
        }
    }

    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getSymbol() { return symbol; }
    public String getExchange() { return exchange; }
    public String getSector() { return sector; }
    public Double getCurrentPrice() { return currentPrice; }
    public Double getPreviousPrice() { return previousPrice; }
    public Long getVolume() { return volume; }

}
