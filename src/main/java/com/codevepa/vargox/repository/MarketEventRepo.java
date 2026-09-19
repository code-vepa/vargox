package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.MarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketEventRepo extends JpaRepository<MarketEvent, Long> {

    List<MarketEvent> findByStockId(Long stockId);
}
