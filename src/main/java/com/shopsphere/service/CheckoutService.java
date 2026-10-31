package com.shopsphere.service;

import com.shopsphere.entity.CustomerOrder;

public interface CheckoutService {

    CustomerOrder placeOrder(Long customerId);

    CustomerOrder placeOrder(Long customerId, String couponCode);

}