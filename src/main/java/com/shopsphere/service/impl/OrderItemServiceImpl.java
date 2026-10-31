
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.service.OrderItemService;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemServiceImpl(
            OrderItemRepository orderItemRepository,
            CustomerOrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderItem addOrderItem(Long orderId, Long productId, Integer quantity) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock available");
        }

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());

        // Reduce product stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Update order total
        order.setTotalAmount(
                (order.getTotalAmount() == null ? 0.0 : order.getTotalAmount())
                + (product.getPrice() * quantity)
        );

        orderRepository.save(order);

        return orderItemRepository.save(item);
    }

    @Override
    public List<OrderItem> getItemsByOrderId(Long orderId) {

        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderItemRepository.findByOrder_Id(orderId);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long itemId) {

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        CustomerOrder order = item.getOrder();
        Product product = item.getProduct();

        // Restore product stock
        product.setStock(product.getStock() + item.getQuantity());
        productRepository.save(product);

        // Update order total
        order.setTotalAmount(
                (order.getTotalAmount() == null ? 0.0 : order.getTotalAmount())
                - (item.getPrice() * item.getQuantity())
        );

        orderRepository.save(order);
        orderItemRepository.delete(item);
    }
}
