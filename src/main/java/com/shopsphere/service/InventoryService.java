package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Inventory;

public interface InventoryService {

    Inventory createInventory(Long productId);

    List<Inventory> getAllInventory();

    Inventory getInventoryByProductId(Long productId);

    Inventory updateStock(Long productId, Integer quantity);

    Inventory updateLowStockThreshold(Long productId, Integer threshold);

    List<Inventory> getLowStockProducts();

    void deleteInventory(Long productId);
}