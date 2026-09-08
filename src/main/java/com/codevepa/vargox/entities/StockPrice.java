package com.codevepa.vargox.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_prices")
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "stock_id", nullable = false, unique = true)
    private Stock stock;

    @Column(nullable = false)
    private double currentPrice;

    @Column(nullable = false)
    private double previousPrice;

    @Column(nullable = false)
    private LocalDateTime currentTimestamp;

    @Column(nullable = false)
    private LocalDateTime previousTimestamp;

    private long volume;

    public StockPrice() {
    }
    
    public StockPrice(
            Stock stock,
            double currentPrice,
            double previousPrice,
            LocalDateTime currentTimestamp,
            LocalDateTime previousTimestamp,
            long volume
    ){
        this.stock = stock;
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
        this.currentTimestamp = currentTimestamp;
        this.previousTimestamp = previousTimestamp;
        this.volume = volume;
    }

    public Long getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(double previousPrice) {
        this.previousPrice = previousPrice;
    }

    public LocalDateTime getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(LocalDateTime currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public LocalDateTime getPreviousTimestamp() {
        return previousTimestamp;
    }

    public void setPreviousTimestamp(LocalDateTime previousTimestamp) {
        this.previousTimestamp = previousTimestamp;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
