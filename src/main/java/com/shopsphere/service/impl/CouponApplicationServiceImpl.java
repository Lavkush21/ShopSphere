package com.shopsphere.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.Coupon;
import com.shopsphere.repository.CouponRepository;
import com.shopsphere.service.CouponApplicationService;

@Service
public class CouponApplicationServiceImpl
        implements CouponApplicationService {

    private final CouponRepository couponRepository;

    public CouponApplicationServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Double applyCoupon(String couponCode, Double orderAmount) {

        if (orderAmount == null || orderAmount <= 0) {
            throw new RuntimeException(
                    "Order amount must be greater than zero");
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                .orElseThrow(() ->
                        new RuntimeException("Invalid coupon code"));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new RuntimeException("Coupon is inactive");
        }

        if (coupon.getExpiryDate() != null
                && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon has expired");
        }

        if (coupon.getUsageLimit() != null
                && (coupon.getUsedCount() == null
                || coupon.getUsedCount() >= coupon.getUsageLimit())) {
            throw new RuntimeException("Coupon usage limit reached");
        }

        double discount;

        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {

            discount = orderAmount * coupon.getDiscountValue() / 100;

        } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {

            discount = coupon.getDiscountValue();

        } else {

            throw new RuntimeException("Invalid discount type");
        }

        discount = Math.min(discount, orderAmount);

        double finalAmount = orderAmount - discount;

        return finalAmount;
    }
}