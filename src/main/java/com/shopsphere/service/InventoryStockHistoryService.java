package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.InventoryStockHistory;

public interface InventoryStockHistoryService {

    List<InventoryStockHistory> getHistoryByProductId(Long productId);

}