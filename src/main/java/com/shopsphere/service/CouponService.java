package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Coupon;

public interface CouponService {

    Coupon createCoupon(Coupon coupon);

    List<Coupon> getAllCoupons();

    Coupon getCouponById(Long id);

    Coupon getCouponByCode(String code);

    Coupon updateCoupon(Long id, Coupon coupon);

    void deleteCoupon(Long id);
}