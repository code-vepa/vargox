package com.codevepa.vargox.service;

import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.repository.MarketEventRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MarketEventService {

    private final MarketEventRepo marketEventRepo;

    public MarketEventService(MarketEventRepo marketEventRepo) {
        this.marketEventRepo = marketEventRepo;
    }

    public MarketEvent createEvent(MarketEvent event) {
        if (event.getStock() == null) {
            throw new IllegalArgumentException("Stock reference is required");
        }
        if (event.getEventType() == null) {
            throw new IllegalArgumentException("Event type is required");
        }
        if (event.getSeverity() == null) {
            throw new IllegalArgumentException("Event severity is required");
        }
        return marketEventRepo.save(event);
    }

    public List<MarketEvent> findAll() {
        return marketEventRepo.findAll();
    }

    public List<MarketEvent> findByStockId(Long stockId) {
        return marketEventRepo.findByStockId(stockId);
    }

    public MarketEvent findById(Long id) {
        return marketEventRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Market event not found with id: " + id));
    }

    public void deleteById(Long id) {
        if (!marketEventRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Market event not found with id: " + id);
        }
        marketEventRepo.deleteById(id);
    }
}