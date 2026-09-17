package com.codevepa.vargox.scheduler;

import com.codevepa.vargox.client.FinnhubClient;
import com.codevepa.vargox.client.FinnhubQuoteResponse;
import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.repository.StockRepo;
import com.codevepa.vargox.service.StockPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockPriceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceScheduler.class);

    private final StockRepo stockRepo;
    private final StockPriceService stockPriceService;
    private final FinnhubClient finnhubClient;

    public StockPriceScheduler(StockRepo stockRepo, StockPriceService stockPriceService, FinnhubClient finnhubClient) {
        this.stockRepo = stockRepo;
        this.stockPriceService = stockPriceService;
        this.finnhubClient = finnhubClient;
    }

    @Scheduled(fixedRate = 60000) // every 60,000ms = 60 seconds
    public void syncAllPrices() {
        List<Stock> stocks = stockRepo.findAll();
        logger.info("Starting price sync for {} stocks", stocks.size());

        for (Stock stock : stocks) {
            try {
                FinnhubQuoteResponse quote = finnhubClient.getQuote(stock.getSymbol());
                stockPriceService.syncFromFinnhub(stock, quote);
                logger.info("Synced price for {}: {}", stock.getSymbol(), quote.getCurrentPrice());
            } catch (Exception e) {
                logger.warn("Failed to sync price for {}: {}", stock.getSymbol(), e.getMessage());
            }
        }

        logger.info("Finished price sync cycle");
    }
}