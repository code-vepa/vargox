package com.codevepa.vargox.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String exchange;

    @Column(nullable = false)
    private String sector;
    

    public Stock() {
    }
    
    public Stock(String symbol, String companyName, String exchange, String sector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.exchange = exchange;
        this.sector = sector;
    }

    /*
        for testing purposes: setId
    */
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
