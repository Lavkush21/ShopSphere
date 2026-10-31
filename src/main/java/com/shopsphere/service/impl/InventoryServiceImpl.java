
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.InventoryStockHistory;
import com.shopsphere.entity.Product;
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
    public Inventory createInventory(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (inventoryRepository.findByProduct_Id(productId).isPresent()) {
            throw new RuntimeException("Inventory already exists for this product");
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
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Override
    @Transactional
    public Inventory updateStock(Long productId, Integer quantity) {

        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        Inventory inventory = getInventoryByProductId(productId);

        Integer previousStock = inventory.getAvailableStock();

        inventory.setAvailableStock(quantity);

        Product product = inventory.getProduct();
        product.setStock(quantity);

        productRepository.save(product);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        if (!previousStock.equals(quantity)) {

            InventoryStockHistory history = new InventoryStockHistory();

            history.setInventory(inventory);
            history.setPreviousStock(previousStock);
            history.setNewStock(quantity);
            history.setChangeQuantity(quantity - previousStock);
            history.setReason("MANUAL_STOCK_UPDATE");

            historyRepository.save(history);
        }

        return updatedInventory;
    }

    @Override
    public Inventory updateLowStockThreshold(Long productId, Integer threshold) {

        if (threshold == null || threshold < 0) {
            throw new RuntimeException("Threshold cannot be negative");
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
                        inventory.getAvailableStock()
                                <= inventory.getLowStockThreshold())
                .toList();
    }

    @Override
    public void deleteInventory(Long productId) {

        Inventory inventory = getInventoryByProductId(productId);
        inventoryRepository.delete(inventory);
    }
}