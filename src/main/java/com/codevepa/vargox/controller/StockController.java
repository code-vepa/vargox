package com.codevepa.vargox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.service.StockService;




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
    public Stock getStockById(@PathVariable Long id){
        return stockService.findById(id);
    }
    
    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        Stock newStock = stockService.createStock(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
