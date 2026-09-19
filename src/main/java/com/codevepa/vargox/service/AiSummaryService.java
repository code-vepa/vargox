package com.codevepa.vargox.service;

import com.codevepa.vargox.client.GeminiClient;
import com.codevepa.vargox.entities.MarketEvent;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.entities.StockPrice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AiSummaryService {

    private final StockService stockService;
    private final StockPriceService stockPriceService;
    private final MarketEventService marketEventService;
    private final GeminiClient geminiClient;

    public AiSummaryService(
            StockService stockService,
            StockPriceService stockPriceService,
            MarketEventService marketEventService,
            GeminiClient geminiClient
    ) {
        this.stockService = stockService;
        this.stockPriceService = stockPriceService;
        this.marketEventService = marketEventService;
        this.geminiClient = geminiClient;
    }

    public String generateSummary(Long stockId) {
        Stock stock = stockService.getStockEntityById(stockId);
        StockPrice price = getPriceOrNull(stockId);
        List<MarketEvent> recentEvents = marketEventService.findRecentByStockId(stockId);

        String prompt = buildPrompt(stock, price, recentEvents);

        try {
            String summary = geminiClient.generateContent(prompt).getFirstText();
            if (summary == null || summary.isBlank()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI summary generation returned no content");
            }
            return summary;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI summary temporarily unavailable, please try again shortly");
        }
    }

    private StockPrice getPriceOrNull(Long stockId) {
        try {
            return stockPriceService.findByStockId(stockId);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildPrompt(Stock stock, StockPrice price, List<MarketEvent> events) {
        StringBuilder sb = new StringBuilder();

        sb.append("Write a brief, 2-3 sentence summary for a stock market app about ")
          .append(stock.getCompanyName())
          .append(" (").append(stock.getSymbol()).append(").\n\n");

        if (price != null) {
            double change = price.getCurrentPrice() - price.getPreviousPrice();
            sb.append("Current price: $").append(price.getCurrentPrice()).append("\n");
            sb.append("Previous price: $").append(price.getPreviousPrice()).append("\n");
            sb.append("Change: ").append(change >= 0 ? "+" : "").append(String.format("%.2f", change)).append("\n\n");
        } else {
            sb.append("No current price data available.\n\n");
        }

        if (events != null && !events.isEmpty()) {
            sb.append("Recent events:\n");
            for (MarketEvent event : events) {
                sb.append("- [").append(event.getSeverity()).append("] ")
                  .append(event.getEventType()).append(": ")
                  .append(event.getDescription()).append("\n");
            }
        } else {
            sb.append("No recent notable events recorded.\n");
        }

        sb.append("\nKeep the tone neutral and informative, like a financial news brief. Do not give investment advice.");

        return sb.toString();
    }
}