package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository 
public interface StockPriceRepo extends JpaRepository<StockPrice, Long> {

    Optional<StockPrice> findByStockId(Long stockId);
}