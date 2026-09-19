package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface StockPriceRepo extends JpaRepository<StockPrice, Long> {

    Optional<StockPrice> findByStockId(Long stockId);
}