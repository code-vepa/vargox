package com.codevepa.vargox.config;

import com.codevepa.vargox.entities.Stock;
import com.codevepa.vargox.repository.StockRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedStocks(StockRepo stockRepo) {
        return args -> {
            List<Stock> starterStocks = List.of(
                new Stock("AAPL", "Apple Inc.", "NASDAQ", "Technology"),
                new Stock("MSFT", "Microsoft Corporation", "NASDAQ", "Technology"),
                new Stock("GOOGL", "Alphabet Inc.", "NASDAQ", "Technology"),
                new Stock("AMZN", "Amazon.com Inc.", "NASDAQ", "Consumer Discretionary"),
                new Stock("NVDA", "NVIDIA Corporation", "NASDAQ", "Technology"),
                new Stock("META", "Meta Platforms Inc.", "NASDAQ", "Technology"),
                new Stock("TSLA", "Tesla, Inc.", "NASDAQ", "Automotive"),
                new Stock("AVGO", "Broadcom Inc.", "NASDAQ", "Technology"),
                new Stock("NFLX", "Netflix, Inc.", "NASDAQ", "Communication Services"),
                new Stock("ADBE", "Adobe Inc.", "NASDAQ", "Technology"),

                new Stock("JPM", "JPMorgan Chase & Co.", "NYSE", "Financial Services"),
                new Stock("V", "Visa Inc.", "NYSE", "Financial Services"),
                new Stock("MA", "Mastercard Incorporated", "NYSE", "Financial Services"),
                new Stock("BAC", "Bank of America Corporation", "NYSE", "Financial Services"),
                new Stock("WFC", "Wells Fargo & Company", "NYSE", "Financial Services"),

                new Stock("JNJ", "Johnson & Johnson", "NYSE", "Healthcare"),
                new Stock("UNH", "UnitedHealth Group Inc.", "NYSE", "Healthcare"),
                new Stock("PFE", "Pfizer Inc.", "NYSE", "Healthcare"),
                new Stock("ABBV", "AbbVie Inc.", "NYSE", "Healthcare"),

                new Stock("WMT", "Walmart Inc.", "NYSE", "Consumer Staples"),
                new Stock("PG", "Procter & Gamble Co.", "NYSE", "Consumer Staples"),
                new Stock("KO", "The Coca-Cola Company", "NYSE", "Consumer Staples"),
                new Stock("PEP", "PepsiCo, Inc.", "NASDAQ", "Consumer Staples"),
                new Stock("COST", "Costco Wholesale Corporation", "NASDAQ", "Consumer Staples"),

                new Stock("XOM", "Exxon Mobil Corporation", "NYSE", "Energy"),
                new Stock("CVX", "Chevron Corporation", "NYSE", "Energy"),

                new Stock("BA", "The Boeing Company", "NYSE", "Industrials"),
                new Stock("CAT", "Caterpillar Inc.", "NYSE", "Industrials"),
                new Stock("DIS", "The Walt Disney Company", "NYSE", "Communication Services"),
                new Stock("HD", "The Home Depot, Inc.", "NYSE", "Consumer Discretionary")
            );

            for (Stock stock : starterStocks) {
                if (stockRepo.findBySymbol(stock.getSymbol()).isEmpty()) {
                    stockRepo.save(stock);
                }
            }
        };
    }
}
