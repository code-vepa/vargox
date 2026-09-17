package com.codevepa.vargox.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.model.StockDetailResponse;
import com.codevepa.vargox.service.StockService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.findAll();
    }

    @GetMapping("/{id}")
    public StockDetailResponse getStockById(@PathVariable Long id) {
        return stockService.findById(id);
    }
    
    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        Stock newStock = stockService.createStock(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        Stock updatedStock = stockService.updateStock(id, stock);
        return new ResponseEntity<>(updatedStock, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Stock> patchStock(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Stock patchedStock = stockService.patchStock(id, updates);
        return new ResponseEntity<>(patchedStock, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
