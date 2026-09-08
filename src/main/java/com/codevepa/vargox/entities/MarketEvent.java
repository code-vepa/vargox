package com.codevepa.vargox.entities;
import com.codevepa.vargox.enums.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_events")
public class MarketEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventSeverity severity;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    public MarketEvent() {
    }

    public MarketEvent(
        Stock stock,
        EventType eventType,
        EventSeverity severity,
        String description,
        LocalDateTime detectedAt
    ){
        this.stock = stock;
        this.eventType = eventType;
        this.severity = severity;
        this.description = description;
        this.detectedAt = detectedAt;
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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(EventSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}
