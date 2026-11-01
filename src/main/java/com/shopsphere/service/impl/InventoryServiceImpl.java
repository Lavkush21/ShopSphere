
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.InventoryStockHistory;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.InventoryRepository;
import com.shopsphere.repository.InventoryStockHistoryRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryStockHistoryRepository historyRepository;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            InventoryStockHistoryRepository historyRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional
    public Inventory createInventory(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with ID: " + productId));

        if (inventoryRepository.findByProduct_Id(productId).isPresent()) {
            throw new RuntimeException(
                    "Inventory already exists for this product");
        }

        if (product.getStock() == null || product.getStock() < 0) {
            throw new RuntimeException(
                    "Product stock cannot be null or negative");
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setAvailableStock(product.getStock());
        inventory.setLowStockThreshold(5);

        return inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getInventoryByProductId(Long productId) {

        return inventoryRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for Product ID: " + productId));
    }

    // Manual stock update
    @Override
    @Transactional
    public Inventory updateStock(Long productId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new RuntimeException(
                    "Stock quantity cannot be null or negative");
        }

        Inventory inventory = getInventoryByProductId(productId);

        return synchronizeStock(
                inventory,
                quantity,
                "MANUAL_STOCK_UPDATE");
    }

    // Stock adjustment
    @Override
    @Transactional
    public Inventory adjustStock(
            Long productId,
            Integer changeQuantity,
            String reason) {

        if (changeQuantity == null || changeQuantity == 0) {
            throw new RuntimeException(
                    "Stock adjustment cannot be null or zero");
        }

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException(
                    "Stock adjustment reason is required");
        }

        Inventory inventory = getInventoryByProductId(productId);

        Integer currentStock = inventory.getAvailableStock();

        if (currentStock == null || currentStock < 0) {
            throw new RuntimeException(
                    "Current stock is invalid");
        }

        int newStock;

        try {
            newStock = Math.addExact(currentStock, changeQuantity);
        } catch (ArithmeticException ex) {
            throw new RuntimeException(
                    "Stock adjustment quantity is too large");
        }

        if (newStock < 0) {
            throw new RuntimeException(
                    "Insufficient inventory stock");
        }

        return synchronizeStock(
                inventory,
                newStock,
                reason.trim());
    }

    // Common method to synchronize Product and Inventory stock
    private Inventory synchronizeStock(
            Inventory inventory,
            Integer newStock,
            String reason) {

        Integer previousStock = inventory.getAvailableStock();

        if (previousStock == null || newStock == null) {
            throw new RuntimeException(
                    "Stock quantity cannot be null");
        }

        if (previousStock < 0 || newStock < 0) {
            throw new RuntimeException(
                    "Stock quantity cannot be negative");
        }

        if (previousStock.equals(newStock)) {
            return inventory;
        }

        Product product = inventory.getProduct();

        inventory.setAvailableStock(newStock);
        product.setStock(newStock);

        productRepository.save(product);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        InventoryStockHistory history = new InventoryStockHistory();

        history.setInventory(updatedInventory);
        history.setPreviousStock(previousStock);
        history.setNewStock(newStock);
        history.setChangeQuantity(newStock - previousStock);
        history.setReason(reason);

        historyRepository.save(history);

        return updatedInventory;
    }

    @Override
    @Transactional
    public Inventory updateLowStockThreshold(
            Long productId,
            Integer threshold) {

        if (threshold == null || threshold < 0) {
            throw new RuntimeException(
                    "Threshold cannot be null or negative");
        }

        Inventory inventory = getInventoryByProductId(productId);

        inventory.setLowStockThreshold(threshold);

        return inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> getLowStockProducts() {

        return inventoryRepository.findAll()
                .stream()
                .filter(inventory ->
                        inventory.getAvailableStock() != null
                        && inventory.getLowStockThreshold() != null
                        && inventory.getAvailableStock()
                                <= inventory.getLowStockThreshold())
                .toList();
    }

    @Override
    @Transactional
    public void deleteInventory(Long productId) {

        Inventory inventory = getInventoryByProductId(productId);

        inventoryRepository.delete(inventory);
    }
}
