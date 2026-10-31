package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.InventoryStockHistory;
import com.shopsphere.repository.InventoryStockHistoryRepository;
import com.shopsphere.service.InventoryStockHistoryService;

@Service
public class InventoryStockHistoryServiceImpl
        implements InventoryStockHistoryService {

    private final InventoryStockHistoryRepository historyRepository;

    public InventoryStockHistoryServiceImpl(
            InventoryStockHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public List<InventoryStockHistory> getHistoryByProductId(Long productId) {

        return historyRepository
                .findByInventory_Product_IdOrderByChangedAtDesc(productId);
    }
}