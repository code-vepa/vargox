package com.codevepa.vargox.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codevepa.vargox.client.FinnhubClient;
import com.codevepa.vargox.client.FinnhubQuoteResponse;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import com.codevepa.vargox.service.StockPriceService;
import com.codevepa.vargox.service.StockService;

@RestController
@RequestMapping("/api/stocks/{stockId}/price")
public class StockPriceController {

    private final StockPriceService stockPriceService;
    private final StockService stockService;
    private final FinnhubClient finnhubClient;

    public StockPriceController(StockPriceService stockPriceService,
            StockService stockService,
            FinnhubClient finnhubClient) {
        this.stockPriceService = stockPriceService;
        this.stockService = stockService;
        this.finnhubClient = finnhubClient;
    }


    @GetMapping
    public StockPrice getPrice(@PathVariable Long stockId) {
        return stockPriceService.findByStockId(stockId);
    }

    @PostMapping
    public ResponseEntity<StockPrice> createPrice(@RequestBody StockPrice stockPrice) {
        StockPrice newPrice = stockPriceService.createStockPrice(stockPrice);
        return new ResponseEntity<>(newPrice, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<StockPrice> updatePrice(@PathVariable Long stockId, @RequestBody StockPrice stockPrice) {
        StockPrice existing = stockPriceService.findByStockId(stockId);
        StockPrice updated = stockPriceService.updateStockPrice(existing.getId(), stockPrice);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<StockPrice> patchPrice(@PathVariable Long stockId, @RequestBody Map<String, Object> updates) {
        StockPrice existing = stockPriceService.findByStockId(stockId);
        StockPrice patched = stockPriceService.patchStockPrice(existing.getId(), updates);
        return new ResponseEntity<>(patched, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePrice(@PathVariable Long stockId) {
        StockPrice existing = stockPriceService.findByStockId(stockId);
        stockPriceService.deleteById(existing.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync")
    public ResponseEntity<StockPrice> syncPrice(@PathVariable Long stockId) {
        Stock stock = stockService.getStockEntityById(stockId);
        FinnhubQuoteResponse quote = finnhubClient.getQuote(stock.getSymbol());
        StockPrice updated = stockPriceService.syncFromFinnhub(stock, quote);
        return ResponseEntity.ok(updated);
    }
}
