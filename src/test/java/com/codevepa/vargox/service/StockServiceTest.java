package com.codevepa.vargox.service;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import com.codevepa.vargox.model.StockDetailResponse;
import com.codevepa.vargox.repository.StockRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepo stockRepo;

    @Mock
    private StockPriceService stockPriceService;

    @InjectMocks
    private StockService stockService;

    private Stock sampleStock;

    @BeforeEach
    void setUp() {
        sampleStock = new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology");
        sampleStock.setId(1L);
    }

    // ---------- createStock ----------

    @Test
    void createStock_shouldSaveAndReturnStock_whenValid() {
        when(stockRepo.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(stockRepo.save(sampleStock)).thenReturn(sampleStock);

        Stock result = stockService.createStock(sampleStock);

        assertThat(result).isEqualTo(sampleStock);
        verify(stockRepo).save(sampleStock);
    }

    @Test
    void createStock_shouldThrow_whenSymbolIsNull() {
        Stock stock = new Stock(null, "Apple Inc.", "NASDAQ", "Technology");

        assertThrows(IllegalArgumentException.class, () -> stockService.createStock(stock));
        verify(stockRepo, never()).save(any());
    }

    @Test
    void createStock_shouldThrow_whenSymbolIsBlank() {
        Stock stock = new Stock("   ", "Apple Inc.", "NASDAQ", "Technology");

        assertThrows(IllegalArgumentException.class, () -> stockService.createStock(stock));
        verify(stockRepo, never()).save(any());
    }

    @Test
    void createStock_shouldThrow_whenSymbolAlreadyExists() {
        when(stockRepo.findBySymbol("AAPL")).thenReturn(Optional.of(sampleStock));

        Stock duplicate = new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> stockService.createStock(duplicate)
        );

        assertThat(exception.getMessage()).contains("AAPL");
        verify(stockRepo, never()).save(any());
    }

    // ---------- findAll ----------

    @Test
    void findAll_shouldReturnAllStocks() {
        Stock second = new Stock("MSFT", "Microsoft Corporation", "NASDAQ", "Technology");
        when(stockRepo.findAll()).thenReturn(List.of(sampleStock, second));

        List<Stock> result = stockService.findAll();

        assertThat(result).hasSize(2).containsExactly(sampleStock, second);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoStocksExist() {
        when(stockRepo.findAll()).thenReturn(List.of());

        List<Stock> result = stockService.findAll();

        assertThat(result).isEmpty();
    }

    // ---------- findById (returns StockDetailResponse) ----------

    @Test
    void findById_shouldReturnDetailResponseWithPrice_whenPriceExists() {
        StockPrice price = new StockPrice(sampleStock, 227.5, 224.1, null, null, 0L);

        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));
        when(stockPriceService.findByStockId(1L)).thenReturn(price);

        //StockDetailResponse is the data transfer object (DTO)
        StockDetailResponse result = stockService.findById(1L);

        assertThat(result.getSymbol()).isEqualTo("AAPL");
        assertThat(result.getCurrentPrice()).isEqualTo(227.5);
        assertThat(result.getPreviousPrice()).isEqualTo(224.1);
    }

    @Test
    void findById_shouldReturnDetailResponseWithNullPrice_whenNoPriceExists() {
        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));
        when(stockPriceService.findByStockId(1L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "not found"));

        StockDetailResponse result = stockService.findById(1L);

        assertThat(result.getSymbol()).isEqualTo("AAPL");
        assertThat(result.getCurrentPrice()).isNull();
        assertThat(result.getPreviousPrice()).isNull();
    }

    @Test
    void findById_shouldThrow404_whenStockDoesNotExist() {
        when(stockRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> stockService.findById(99L)
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    // ---------- getStockEntityById ----------

    @Test
    void getStockEntityById_shouldReturnStock_whenExists() {
        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));

        Stock result = stockService.getStockEntityById(1L);

        assertThat(result).isEqualTo(sampleStock);
    }

    @Test
    void getStockEntityById_shouldThrow404_whenNotFound() {
        when(stockRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> stockService.getStockEntityById(99L));
    }

    // ---------- updateStock ----------

    @Test
    void updateStock_shouldUpdateAllFields_whenStockExists() {
        Stock updatedData = new Stock("AAPL", "Apple Incorporated", "NASDAQ", "Consumer Electronics");

        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));
        when(stockRepo.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stock result = stockService.updateStock(1L, updatedData);

        assertThat(result.getCompanyName()).isEqualTo("Apple Incorporated");
        assertThat(result.getSector()).isEqualTo("Consumer Electronics");
        assertThat(result.getId()).isEqualTo(1L); // id must remain unchanged
    }

    @Test
    void updateStock_shouldThrow404_whenStockDoesNotExist() {
        Stock updatedData = new Stock("AAPL", "Apple Incorporated", "NASDAQ", "Technology");
        when(stockRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> stockService.updateStock(99L, updatedData));
        verify(stockRepo, never()).save(any());
    }

    // ---------- patchStock ----------

    @Test
    void patchStock_shouldUpdateOnlyProvidedField() {
        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));
        when(stockRepo.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stock result = stockService.patchStock(1L, Map.of("sector", "Consumer Electronics"));

        assertThat(result.getSector()).isEqualTo("Consumer Electronics");
        assertThat(result.getSymbol()).isEqualTo("AAPL"); // untouched fields remain the same
        assertThat(result.getCompanyName()).isEqualTo("Apple Inc.");
    }

    @Test
    void patchStock_shouldThrow_whenFieldIsUnknown() {
        when(stockRepo.findById(1L)).thenReturn(Optional.of(sampleStock));

        assertThrows(IllegalArgumentException.class,
                () -> stockService.patchStock(1L, Map.of("madeUpField", "value")));

        verify(stockRepo, never()).save(any());
    }

    @Test
    void patchStock_shouldThrow404_whenStockDoesNotExist() {
        when(stockRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> stockService.patchStock(99L, Map.of("sector", "Technology")));
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_shouldCallRepoDelete_whenStockExists() {
        when(stockRepo.existsById(1L)).thenReturn(true);

        stockService.deleteById(1L);

        verify(stockRepo).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrow404_whenStockDoesNotExist() {
        when(stockRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> stockService.deleteById(99L));
        verify(stockRepo, never()).deleteById(any());
    }
}
