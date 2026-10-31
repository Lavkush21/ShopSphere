package com.shopsphere.service;

public interface CouponApplicationService {

    Double applyCoupon(String couponCode, Double orderAmount);

}