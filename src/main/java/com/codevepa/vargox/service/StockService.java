package com.codevepa.vargox.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.repository.StockRepo;


@Service
public class StockService {
    private final StockRepo stockRepo;

    public StockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    public Stock createStock(Stock stock) {
        if (stock.getSymbol() == null || stock.getSymbol().isBlank()) {
            throw new IllegalArgumentException("Stock symbol is required");
        }
        if (stockRepo.findBySymbol(stock.getSymbol()).isPresent()) {
            throw new IllegalArgumentException("Stock with symbol " +
                stock.getSymbol() + " already exists");
        }
        return stockRepo.save(stock);
    }

    public List<Stock> findAll() {
        return stockRepo.findAll();
    }

    public Stock findById(Long id) {
        return stockRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Stock not found with id: " + id));
    }
    

    public Stock updateStock(Long id, Stock updatedStock) {
        Stock existingStock = stockRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Stock not found with id: " + id));

        existingStock.setSymbol(updatedStock.getSymbol());
        existingStock.setCompanyName(updatedStock.getCompanyName());
        existingStock.setExchange(updatedStock.getExchange());
        existingStock.setSector(updatedStock.getSector());

        return stockRepo.save(existingStock);
    }


    public void deleteById(Long id) {
        if (!stockRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Stock not found with id: " + id);
        }
        stockRepo.deleteById(id);
    }
}
