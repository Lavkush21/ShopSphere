package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.InventoryStockHistory;

@Repository
public interface InventoryStockHistoryRepository
        extends JpaRepository<InventoryStockHistory, Long> {

    List<InventoryStockHistory> findByInventory_Product_IdOrderByChangedAtDesc(
            Long productId
    );
}