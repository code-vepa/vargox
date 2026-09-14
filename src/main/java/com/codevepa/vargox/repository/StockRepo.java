package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.Stock;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepo extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    List<Stock> findAll();
}
