package com.shopsphere.service;

import com.shopsphere.entity.CustomerOrder;

public interface CheckoutService {

    CustomerOrder placeOrder(Long customerId);

}