package com.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Inventory;
import com.shopsphere.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Create Inventory
    @PostMapping("/{productId}")
    public Inventory createInventory(@PathVariable Long productId) {
        return inventoryService.createInventory(productId);
    }

    // Get All Inventory
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // Get Inventory By Product ID
    @GetMapping("/product/{productId}")
    public Inventory getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId);
    }

    // Update Stock
    @PutMapping("/product/{productId}/stock")
    public Inventory updateStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return inventoryService.updateStock(productId, quantity);
    }

    // Update Low Stock Threshold
    @PutMapping("/product/{productId}/threshold")
    public Inventory updateThreshold(
            @PathVariable Long productId,
            @RequestParam Integer threshold) {

        return inventoryService.updateLowStockThreshold(productId, threshold);
    }

    // Get Low Stock Products
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockProducts() {
        return inventoryService.getLowStockProducts();
    }

    // Delete Inventory
    @DeleteMapping("/product/{productId}")
    public String deleteInventory(@PathVariable Long productId) {
        inventoryService.deleteInventory(productId);
        return "Inventory deleted successfully";
    }
}