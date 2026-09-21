package com.codevepa.vargox.service;

import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.enums.EventSeverity;
import com.codevepa.vargox.enums.EventType;
import com.codevepa.vargox.repository.MarketEventRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketEventServiceTest {

    @Mock
    private MarketEventRepo marketEventRepo;

    @InjectMocks
    private MarketEventService marketEventService;

    private Stock sampleStock;
    private MarketEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleStock = new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology");
        sampleStock.setId(1L);

        sampleEvent = new MarketEvent(
                sampleStock,
                EventType.PRICE_SPIKE,
                EventSeverity.HIGH,
                "Apple reported Q3 earnings above analyst expectations.",
                LocalDateTime.of(2026, 9, 19, 9, 30)
        );
    }

    // test for createEvent

    @Test
    void createEvent_shouldSaveAndReturnEvent_whenValid() {
        when(marketEventRepo.save(sampleEvent)).thenReturn(sampleEvent);

        MarketEvent result = marketEventService.createEvent(sampleEvent);

        assertThat(result).isEqualTo(sampleEvent);
        verify(marketEventRepo).save(sampleEvent);
    }

    @Test
    void createEvent_shouldThrow_whenStockIsNull() {
        MarketEvent event = new MarketEvent(
                null, EventType.PRICE_SPIKE, EventSeverity.HIGH, "Some description", LocalDateTime.now()
        );

        assertThrows(IllegalArgumentException.class, () -> marketEventService.createEvent(event));
        verify(marketEventRepo, never()).save(any());
    }

    @Test
    void createEvent_shouldThrow_whenEventTypeIsNull() {
        MarketEvent event = new MarketEvent(
                sampleStock, null, EventSeverity.HIGH, "Some description", LocalDateTime.now()
        );

        assertThrows(IllegalArgumentException.class, () -> marketEventService.createEvent(event));
        verify(marketEventRepo, never()).save(any());
    }

    @Test
    void createEvent_shouldThrow_whenSeverityIsNull() {
        MarketEvent event = new MarketEvent(
                sampleStock, EventType.PRICE_SPIKE, null, "Some description", LocalDateTime.now()
        );

        assertThrows(IllegalArgumentException.class, () -> marketEventService.createEvent(event));
        verify(marketEventRepo, never()).save(any());
    }

    // ---------- findAll ----------

    @Test
    void findAll_shouldReturnAllEvents() {
        when(marketEventRepo.findAll()).thenReturn(List.of(sampleEvent));

        List<MarketEvent> result = marketEventService.findAll();

        assertThat(result).hasSize(1).containsExactly(sampleEvent);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEventsExist() {
        when(marketEventRepo.findAll()).thenReturn(List.of());

        List<MarketEvent> result = marketEventService.findAll();

        assertThat(result).isEmpty();
    }

    // test for findByStockId

    @Test
    void findByStockId_shouldReturnEventsForThatStock() {
        when(marketEventRepo.findByStockId(1L)).thenReturn(List.of(sampleEvent));

        List<MarketEvent> result = marketEventService.findByStockId(1L);

        assertThat(result).hasSize(1).containsExactly(sampleEvent);
    }

    @Test
    void findByStockId_shouldReturnEmptyList_whenStockHasNoEvents() {
        when(marketEventRepo.findByStockId(2L)).thenReturn(List.of());

        List<MarketEvent> result = marketEventService.findByStockId(2L);

        assertThat(result).isEmpty();
    }

    // test for findById

    @Test
    void findById_shouldReturnEvent_whenExists() {
        when(marketEventRepo.findById(1L)).thenReturn(Optional.of(sampleEvent));

        MarketEvent result = marketEventService.findById(1L);

        assertThat(result).isEqualTo(sampleEvent);
    }

    @Test
    void findById_shouldThrow404_whenNotFound() {
        when(marketEventRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> marketEventService.findById(99L)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    // test for findRecentByStockId

    @Test
    void findRecentByStockId_shouldReturnTop3Events() {
        MarketEvent second = new MarketEvent(
                sampleStock, EventType.PRICE_SPIKE, EventSeverity.LOW, "Minor fluctuation", LocalDateTime.now()
        );
        MarketEvent third = new MarketEvent(
                sampleStock, EventType.PRICE_SPIKE, EventSeverity.MEDIUM, "Moderate movement", LocalDateTime.now()
        );

        when(marketEventRepo.findTop3ByStockIdOrderByDetectedAtDesc(1L))
                .thenReturn(List.of(sampleEvent, second, third));

        List<MarketEvent> result = marketEventService.findRecentByStockId(1L);

        assertThat(result).hasSize(3).containsExactly(sampleEvent, second, third);
    }

    @Test
    void findRecentByStockId_shouldReturnEmptyList_whenNoEventsExist() {
        when(marketEventRepo.findTop3ByStockIdOrderByDetectedAtDesc(1L)).thenReturn(List.of());

        List<MarketEvent> result = marketEventService.findRecentByStockId(1L);

        assertThat(result).isEmpty();
    }

    // test for deleteById

    @Test
    void deleteById_shouldCallRepoDelete_whenExists() {
        when(marketEventRepo.existsById(1L)).thenReturn(true);

        marketEventService.deleteById(1L);

        verify(marketEventRepo).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrow404_whenNotFound() {
        when(marketEventRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> marketEventService.deleteById(99L));
        verify(marketEventRepo, never()).deleteById(any());
    }
}