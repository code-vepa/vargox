package com.codevepa.vargox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.codevepa.vargox.client.FinnhubQuoteResponse;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import com.codevepa.vargox.repository.StockPriceRepo;

@ExtendWith(MockitoExtension.class)
public class StockPriceServiceTest {
    
    @Mock
    private StockPriceRepo stockPriceRepo;

    @InjectMocks 
    private StockPriceService stockPriceService;

    private StockPrice samplePrice;
    private Stock sampleStock;

    @BeforeEach 
    void setUp() {
        sampleStock = new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology");
        sampleStock.setId(1L);

        samplePrice = new StockPrice(
                sampleStock,
                227.5,
                224.1,
                LocalDateTime.of(2026, 9, 19, 10, 0),
                LocalDateTime.of(2026, 9, 19, 9, 0),
                0L);
        samplePrice.setId(1L);
    }
    
    // test for createStock

    @Test 
    void createStockPrice_shouldSave_whenValid() {
        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.empty());
        when(stockPriceRepo.save(samplePrice)).thenReturn(samplePrice);

        StockPrice result = stockPriceService.createStockPrice(samplePrice);
        assertThat(result).isEqualTo(samplePrice);

        verify(stockPriceRepo).save(samplePrice);
    }

    @Test 
    void createStockPrice_shouldThrow_whenStockIsNull() {
        StockPrice priceWithoutStock = new StockPrice(
                null, 227.5, 224.1, LocalDateTime.now(), LocalDateTime.now(), 0L);

        assertThrows(IllegalArgumentException.class,
                () -> stockPriceService.createStockPrice(priceWithoutStock));

        verify(stockPriceRepo, never()).save(any());
    }
    
    @Test 
    void createStockPrice_shouldThrow_whenPriceAlreadyExistsForStock() {
        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.of(samplePrice));

        assertThrows(IllegalArgumentException.class,
                () -> stockPriceService.createStockPrice(samplePrice));

        verify(stockPriceRepo, never()).save(any());
    }
    
    // test for findAll
    @Test
    void findAll_shouldReturnAllPrices() {
        when(stockPriceRepo.findAll()).thenReturn(List.of(samplePrice));

        List<StockPrice> result = stockPriceService.findAll();

        assertThat(result).hasSize(1).containsExactly(samplePrice);
    }

    @Test 
    void findAll_shouldReturnEmptyList_whenNoPricesExist() {
        when(stockPriceRepo.findAll()).thenReturn(List.of());

        List<StockPrice> result = stockPriceService.findAll();

        assertThat(result).isEmpty();
    }

    // test for findById
    @Test
    void findById_shouldReturnPrice_whenExists() {
        when(stockPriceRepo.findById(1L)).thenReturn(Optional.of(samplePrice));

        StockPrice result = stockPriceService.findById(1L);

        assertThat(result).isEqualTo(samplePrice);
    }
    
    @Test
    void findById_shouldThrow404_whenNotFound() {
        when(stockPriceRepo.findById(99L)).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> stockPriceService.findById(99L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    // test for findByStockId
    @Test
    void findByStockId_shouldReturnPrice_whenExists() {
        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.of(samplePrice));

        StockPrice result = stockPriceService.findByStockId(1L);

        assertThat(result).isEqualTo(samplePrice);
    }

    @Test
    void findByStockId_shouldThrow404_whenNotFound() {
        when(stockPriceRepo.findByStockId(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> stockPriceService.findByStockId(99L));
    }
    
    // test for updateStockPrice

    @Test 
    void updateStockPrice_shouldUpdateAllFields_whenExists() {
        StockPrice updatedData = new StockPrice(
                sampleStock, 230.0, 227.5,
                LocalDateTime.of(2026, 9, 19, 11, 0),
                LocalDateTime.of(2026, 9, 19, 10, 0),
                500L);

        when(stockPriceRepo.findById(1L)).thenReturn(Optional.of(samplePrice));
        when(stockPriceRepo.save(any(StockPrice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        StockPrice result = stockPriceService.updateStockPrice(1L, updatedData);

        assertThat(result.getCurrentPrice()).isEqualTo(230.0);
        assertThat(result.getPreviousPrice()).isEqualTo(227.5);
        assertThat(result.getVolume()).isEqualTo(500L);
    }
    
    @Test
    void updateStockPrice_shouldThrow404_whenNotFound() {
        StockPrice updatedData = new StockPrice(
                sampleStock, 230.0,
                227.5,
                LocalDateTime.now(), LocalDateTime.now(),
                0L);
        when(stockPriceRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> stockPriceService.updateStockPrice(99L, updatedData));
        verify(stockPriceRepo, never()).save(any());
    }
    
    // test for patchStockPrice
    @Test
    void patchStockPrice_shouldUpdateOnlyCurrentPrice() {
        when(stockPriceRepo.findById(1L)).thenReturn(Optional.of(samplePrice));
        when(stockPriceRepo.save(any(StockPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockPrice result = stockPriceService.patchStockPrice(1L, Map.of("currentPrice", 235.75));

        assertThat(result.getCurrentPrice()).isEqualTo(235.75);
        assertThat(result.getPreviousPrice()).isEqualTo(224.1); // untouched
    }

    @Test
    void patchStockPrice_shouldParseTimestampFields() {
        when(stockPriceRepo.findById(1L)).thenReturn(Optional.of(samplePrice));
        when(stockPriceRepo.save(any(StockPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockPrice result = stockPriceService.patchStockPrice(
                1L, Map.of("currentTimestamp", "2026-09-19T12:00:00"));

        assertThat(result.getCurrentTimestamp()).isEqualTo(LocalDateTime.of(2026, 9, 19, 12, 0));
    }
    
    @Test
    void patchStockPrice_shouldThrow_whenFieldIsUnknown() {
        when(stockPriceRepo.findById(1L)).thenReturn(Optional.of(samplePrice));

        assertThrows(IllegalArgumentException.class,
                () -> stockPriceService.patchStockPrice(1L, Map.of("madeUpField", "value")));
        verify(stockPriceRepo, never()).save(any());
    }

    @Test
    void patchStockPrice_shouldThrow404_whenNotFound() {
        when(stockPriceRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> stockPriceService.patchStockPrice(99L, Map.of("currentPrice", 100.0)));
    }

    // test for deleteById
    @Test 
    void deleteById_shouldDelete_whenExists() {
        when(stockPriceRepo.existsById(1L)).thenReturn(true);

        stockPriceService.deleteById(1L);
        verify(stockPriceRepo).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrow404_whenNotFound() {
        when(stockPriceRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> stockPriceService.deleteById(99L));
        verify(stockPriceRepo, never()).deleteById(any());
    }

    // test for syncFromFinnhub (external API)

    @Test
    void syncFromFinnhub_shouldCreateNewPrice_whenNoneExistsYet() {
        FinnhubQuoteResponse quote = mock(FinnhubQuoteResponse.class);
        when(quote.getCurrentPrice()).thenReturn(227.5);
        when(quote.getPreviousClose()).thenReturn(224.1);

        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.empty());
        when(stockPriceRepo.save(any(StockPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockPrice result = stockPriceService.syncFromFinnhub(sampleStock, quote);

        assertThat(result.getCurrentPrice()).isEqualTo(227.5);
        assertThat(result.getPreviousPrice()).isEqualTo(224.1);
        assertThat(result.getStock()).isEqualTo(sampleStock);
    }

    @Test
    void syncFromFinnhub_shouldShiftCurrentToPrevious_whenPriceAlreadyExists() {
        FinnhubQuoteResponse quote = mock(FinnhubQuoteResponse.class);
        when(quote.getCurrentPrice()).thenReturn(230.0);

        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.of(samplePrice));
        when(stockPriceRepo.save(any(StockPrice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StockPrice result = stockPriceService.syncFromFinnhub(sampleStock, quote);

        // old current (227.5) becomes new previous
        assertThat(result.getPreviousPrice()).isEqualTo(227.5);
        // new current comes from the quote
        assertThat(result.getCurrentPrice()).isEqualTo(230.0);
    }

    @Test
    void syncFromFinnhub_shouldUpdateTimestamps_whenPriceAlreadyExists() {
        FinnhubQuoteResponse quote = mock(FinnhubQuoteResponse.class);
        when(quote.getCurrentPrice()).thenReturn(230.0);

        LocalDateTime oldCurrentTimestamp = samplePrice.getCurrentTimestamp();

        when(stockPriceRepo.findByStockId(1L)).thenReturn(Optional.of(samplePrice));
        ArgumentCaptor<StockPrice> captor = ArgumentCaptor.forClass(StockPrice.class);
        when(stockPriceRepo.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        stockPriceService.syncFromFinnhub(sampleStock, quote);

        StockPrice saved = captor.getValue();
        assertThat(saved.getPreviousTimestamp()).isEqualTo(oldCurrentTimestamp);
        assertThat(saved.getCurrentTimestamp()).isAfter(oldCurrentTimestamp);
    }
}
