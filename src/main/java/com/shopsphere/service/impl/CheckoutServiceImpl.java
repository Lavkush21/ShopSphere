
package com.shopsphere.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Coupon;
import com.shopsphere.entity.Customer;
import com.shopsphere.entity.CustomerOrder;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;

import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.repository.CustomerOrderRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.CouponRepository;

import com.shopsphere.service.CheckoutService;
import com.shopsphere.service.CouponApplicationService;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final CouponApplicationService couponApplicationService;

    public CheckoutServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CustomerRepository customerRepository,
            CustomerOrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CouponRepository couponRepository,
            CouponApplicationService couponApplicationService) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.couponApplicationService = couponApplicationService;
    }

    @Override
    @Transactional
    public CustomerOrder placeOrder(Long customerId) {
        return placeOrderInternal(customerId, null);
    }

    @Override
    @Transactional
    public CustomerOrder placeOrder(Long customerId, String couponCode) {
        return placeOrderInternal(customerId, couponCode);
    }

    private CustomerOrder placeOrderInternal(
            Long customerId, String couponCode) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart_Id(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = 0;

        // Validate stock and calculate original total
        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findById(
                    cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName());
            }

            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        // Apply coupon discount if coupon code is provided
        if (couponCode != null && !couponCode.isBlank()) {
            totalAmount = couponApplicationService.applyCoupon(
                    couponCode, totalAmount);
        }

        // Create order
        CustomerOrder order = new CustomerOrder();

        order.setCustomer(customer);
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        CustomerOrder savedOrder = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findById(
                    cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            product.setStock(
                    product.getStock() - cartItem.getQuantity());

            productRepository.save(product);

            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            OrderItem savedItem = orderItemRepository.save(orderItem);

            savedOrder.addOrderItem(savedItem);
        }

        // Clear cart after successful checkout
        cartItemRepository.deleteByCart_Id(cart.getId());

        // Increment coupon usage only after successful order processing
        if (couponCode != null && !couponCode.isBlank()) {

            Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                    .orElseThrow(() ->
                            new RuntimeException("Coupon not found"));

            coupon.setUsedCount(
                    coupon.getUsedCount() == null
                            ? 1
                            : coupon.getUsedCount() + 1);

            couponRepository.save(coupon);
        }

        return savedOrder;
    }
}
