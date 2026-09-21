package com.codevepa.vargox.service;

import com.codevepa.vargox.client.GeminiClient;
import com.codevepa.vargox.client.GeminiResponse;
import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import com.codevepa.vargox.enums.EventSeverity;
import com.codevepa.vargox.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiSummaryServiceTest {

    @Mock
    private StockService stockService;

    @Mock
    private StockPriceService stockPriceService;

    @Mock
    private MarketEventService marketEventService;

    @Mock
    private GeminiClient geminiClient;

    @InjectMocks
    private AiSummaryService aiSummaryService;

    private Stock sampleStock;
    private StockPrice samplePrice;

    @BeforeEach
    void setUp() {
        sampleStock = new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology");
        sampleStock.setId(1L);

        samplePrice = new StockPrice(
                sampleStock, 227.5, 224.1,
                LocalDateTime.of(2026, 9, 19, 10, 0),
                LocalDateTime.of(2026, 9, 19, 9, 0),
                0L
        );
    }

    // test for generateSummary - Valid outcomes

    @Test
    void generateSummary_shouldReturnSummary_whenAllDataPresent() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("Apple is trading up slightly today.");
        when(geminiClient.generateContent(any())).thenReturn(geminiResponse);

        String result = aiSummaryService.generateSummary(1L);

        assertThat(result).isEqualTo("Apple is trading up slightly today.");
    }

    @Test
    void generateSummary_shouldStillWork_whenPriceIsMissing() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "no price"));
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("No price data yet for Apple.");
        when(geminiClient.generateContent(any())).thenReturn(geminiResponse);

        String result = aiSummaryService.generateSummary(1L);

        assertThat(result).isEqualTo("No price data yet for Apple.");
    }

    // test for prompt content checks

    @Test
    void generateSummary_shouldIncludePriceDetailsInPrompt() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("summary");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        when(geminiClient.generateContent(promptCaptor.capture())).thenReturn(geminiResponse);

        aiSummaryService.generateSummary(1L);

        String prompt = promptCaptor.getValue();
        assertThat(prompt).contains("Apple Inc.");
        assertThat(prompt).contains("AAPL");
        assertThat(prompt).contains("Current price: $227.5");
        assertThat(prompt).contains("Previous price: $224.1");
    }

    @Test
    void generateSummary_shouldNoteMissingPriceInPrompt_whenPriceIsNull() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "no price"));
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("summary");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        when(geminiClient.generateContent(promptCaptor.capture())).thenReturn(geminiResponse);

        aiSummaryService.generateSummary(1L);

        assertThat(promptCaptor.getValue()).contains("No current price data available.");
    }

    @Test
    void generateSummary_shouldIncludeEventsInPrompt_whenEventsExist() {
        MarketEvent event = new MarketEvent(
                sampleStock, EventType.PRICE_SPIKE, EventSeverity.HIGH,
                "Apple reported strong Q3 earnings.", LocalDateTime.now()
        );

        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of(event));

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("summary");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        when(geminiClient.generateContent(promptCaptor.capture())).thenReturn(geminiResponse);

        aiSummaryService.generateSummary(1L);

        String prompt = promptCaptor.getValue();
        assertThat(prompt).contains("Recent events:");
        assertThat(prompt).contains("Apple reported strong Q3 earnings.");
    }

    @Test
    void generateSummary_shouldNoteNoEventsInPrompt_whenEventListIsEmpty() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("summary");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        when(geminiClient.generateContent(promptCaptor.capture())).thenReturn(geminiResponse);

        aiSummaryService.generateSummary(1L);

        assertThat(promptCaptor.getValue()).contains("No recent notable events recorded.");
    }

    // test for failure handling

    @Test
    void generateSummary_shouldThrow503_whenGeminiReturnsNullText() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn(null);
        when(geminiClient.generateContent(any())).thenReturn(geminiResponse);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiSummaryService.generateSummary(1L)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(503);
    }

    @Test
    void generateSummary_shouldThrow503_whenGeminiReturnsBlankText() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        GeminiResponse geminiResponse = mock(GeminiResponse.class);
        when(geminiResponse.getFirstText()).thenReturn("   ");
        when(geminiClient.generateContent(any())).thenReturn(geminiResponse);

        assertThrows(ResponseStatusException.class, () -> aiSummaryService.generateSummary(1L));
    }

    @Test
    void generateSummary_shouldThrow503_whenGeminiClientThrowsException() {
        when(stockService.getStockEntityById(1L)).thenReturn(sampleStock);
        when(stockPriceService.findByStockId(1L)).thenReturn(samplePrice);
        when(marketEventService.findRecentByStockId(1L)).thenReturn(List.of());

        when(geminiClient.generateContent(any())).thenThrow(new RuntimeException("Network error"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiSummaryService.generateSummary(1L)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(503);
    }

    @Test
    void generateSummary_shouldPropagate404_whenStockDoesNotExist() {
        when(stockService.getStockEntityById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found with id: 99"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiSummaryService.generateSummary(99L)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }
}
