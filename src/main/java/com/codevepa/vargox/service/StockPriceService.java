package com.codevepa.vargox.service;

import com.codevepa.vargox.client.FinnhubQuoteResponse;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import com.codevepa.vargox.repository.StockPriceRepo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StockPriceService {

    private final StockPriceRepo stockPriceRepo;


    public StockPriceService(StockPriceRepo stockPriceRepo) {
        this.stockPriceRepo = stockPriceRepo;
    }

    public StockPrice createStockPrice(StockPrice stockPrice) {
        if (stockPrice.getStock() == null) {
            throw new IllegalArgumentException("Stock reference is required");
        }
        if (stockPriceRepo.findByStockId(stockPrice.getStock().getId()).isPresent()) {
            throw new IllegalArgumentException("Stock price already exists for stock id: "
                + stockPrice.getStock().getId());
        }
        return stockPriceRepo.save(stockPrice);
    }

    public List<StockPrice> findAll() {
        return stockPriceRepo.findAll();
    }

    public StockPrice findById(Long id) {
        return stockPriceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Stock price not found with id: " + id));
    }

    public StockPrice findByStockId(Long stockId) {
        return stockPriceRepo.findByStockId(stockId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Stock price not found for stock id: " + stockId));
    }

    public StockPrice updateStockPrice(Long id, StockPrice updatedStockPrice) {
        StockPrice existing = stockPriceRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Stock price not found with id: " + id));

        existing.setCurrentPrice(updatedStockPrice.getCurrentPrice());
        existing.setPreviousPrice(updatedStockPrice.getPreviousPrice());
        existing.setCurrentTimestamp(updatedStockPrice.getCurrentTimestamp());
        existing.setPreviousTimestamp(updatedStockPrice.getPreviousTimestamp());
        existing.setVolume(updatedStockPrice.getVolume());

        return stockPriceRepo.save(existing);
    }

    public StockPrice patchStockPrice(Long id, Map<String, Object> updates) {
        StockPrice existing = stockPriceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock price not found with id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "currentPrice" -> existing.setCurrentPrice(((Number) value).doubleValue());
                case "previousPrice" -> existing.setPreviousPrice(((Number) value).doubleValue());
                case "currentTimestamp" -> existing.setCurrentTimestamp(LocalDateTime.parse((String) value));
                case "previousTimestamp" -> existing.setPreviousTimestamp(LocalDateTime.parse((String) value));
                case "volume" -> existing.setVolume(((Number) value).longValue());
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        });

        return stockPriceRepo.save(existing);
    }

    public void deleteById(Long id) {
        if (!stockPriceRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock price not found with id: " + id);
        }
        stockPriceRepo.deleteById(id);
    }

    public StockPrice syncFromFinnhub(Stock stock, FinnhubQuoteResponse quote) {
        StockPrice existing = stockPriceRepo.findByStockId(stock.getId()).orElse(null);
        LocalDateTime now = LocalDateTime.now();

        if (existing == null) {
            StockPrice newPrice = new StockPrice(
                    stock, quote.getCurrentPrice(), quote.getPreviousClose(), now, now, 0L);
            return stockPriceRepo.save(newPrice);
        }
        
        if (quote.getCurrentPrice() != existing.getCurrentPrice()) {
            existing.setPreviousPrice(existing.getCurrentPrice());
            existing.setPreviousTimestamp(existing.getCurrentTimestamp());
            existing.setCurrentPrice(quote.getCurrentPrice());
            existing.setCurrentTimestamp(now);
        }

        return stockPriceRepo.save(existing);
    }
    
}