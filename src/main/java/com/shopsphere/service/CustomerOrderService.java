
package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.CustomerOrder;

public interface CustomerOrderService {

    CustomerOrder createOrder(CustomerOrder order, Long customerId);

    List<CustomerOrder> getAllOrders();

    CustomerOrder getOrderById(Long id);

    List<CustomerOrder> getOrdersByCustomerId(Long customerId);

    CustomerOrder updateOrder(Long id, CustomerOrder order);

    CustomerOrder updateOrderStatus(Long id, String status);

    CustomerOrder cancelOrder(Long id);

    void deleteOrder(Long id);
}
