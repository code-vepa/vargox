package com.codevepa.vargox.model;

import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.enums.EventSeverity;
import com.codevepa.vargox.enums.EventType;

import java.time.LocalDateTime;

public class MarketEventResponse {

    private Long id;
    private Long stockId;
    private EventType eventType;
    private EventSeverity severity;
    private String description;
    private LocalDateTime detectedAt;

    public MarketEventResponse(MarketEvent event) {
        this.id = event.getId();
        this.stockId = event.getStock().getId();
        this.eventType = event.getEventType();
        this.severity = event.getSeverity();
        this.description = event.getDescription();
        this.detectedAt = event.getDetectedAt();
    }

    public Long getId() { return id; }
    public Long getStockId() { return stockId; }
    public EventType getEventType() { return eventType; }
    public EventSeverity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
}