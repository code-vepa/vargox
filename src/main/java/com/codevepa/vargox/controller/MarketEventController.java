package com.codevepa.vargox.controller;

import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.model.MarketEventResponse;
import com.codevepa.vargox.service.MarketEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks/{stockId}/events")
public class MarketEventController {

    private final MarketEventService marketEventService;

    public MarketEventController(MarketEventService marketEventService) {
        this.marketEventService = marketEventService;
    }

    @GetMapping
    public List<MarketEventResponse> getEventsForStock(@PathVariable Long stockId) {
        return marketEventService.findByStockId(stockId).stream()
                .map(MarketEventResponse::new)
                .toList();
    }

    @PostMapping
    public ResponseEntity<MarketEventResponse> createEvent(@RequestBody MarketEvent event) {
        MarketEvent newEvent = marketEventService.createEvent(event);
        return new ResponseEntity<>(new MarketEventResponse(newEvent), HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long stockId, @PathVariable Long eventId) {
        marketEventService.deleteById(eventId);
        return ResponseEntity.noContent().build();
    }
}
