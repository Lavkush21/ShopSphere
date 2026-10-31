
package com.shopsphere.controller;

import com.shopsphere.entity.InventoryStockHistory;
import com.shopsphere.service.InventoryStockHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/history")
public class InventoryStockHistoryController {

    private final InventoryStockHistoryService historyService;

    public InventoryStockHistoryController(
            InventoryStockHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/product/{productId}")
    public List<InventoryStockHistory> getHistoryByProductId(
            @PathVariable Long productId) {

        return historyService.getHistoryByProductId(productId);
    }
}
