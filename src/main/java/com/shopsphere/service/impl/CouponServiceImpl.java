
package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.Coupon;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CouponRepository;
import com.shopsphere.service.CouponService;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {

        if (couponRepository.existsByCodeIgnoreCase(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }

        validateCoupon(coupon);

        coupon.setCode(coupon.getCode().trim().toUpperCase());
        coupon.setUsedCount(0);
        coupon.setActive(true);

        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with ID: " + id));
    }

    @Override
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with code: " + code));
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon coupon) {

        Coupon existingCoupon = getCouponById(id);

        validateCoupon(coupon);

        existingCoupon.setDiscountValue(coupon.getDiscountValue());
        existingCoupon.setDiscountType(coupon.getDiscountType().toUpperCase());
        existingCoupon.setExpiryDate(coupon.getExpiryDate());
        existingCoupon.setUsageLimit(coupon.getUsageLimit());
        existingCoupon.setActive(coupon.getActive());

        return couponRepository.save(existingCoupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }

    private void validateCoupon(Coupon coupon) {

        if (coupon.getDiscountValue() == null
                || coupon.getDiscountValue() <= 0) {
            throw new RuntimeException(
                    "Discount value must be greater than zero");
        }

        if (coupon.getDiscountType() == null) {
            throw new RuntimeException("Discount type is required");
        }

        String type = coupon.getDiscountType().toUpperCase();

        if (!type.equals("PERCENTAGE") && !type.equals("FIXED")) {
            throw new RuntimeException(
                    "Discount type must be PERCENTAGE or FIXED");
        }

        if (type.equals("PERCENTAGE") && coupon.getDiscountValue() > 100) {
            throw new RuntimeException(
                    "Percentage discount cannot exceed 100");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
            throw new RuntimeException(
                    "Usage limit must be greater than zero");
        }
    }
}
