package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Coupon;
import com.shopsphere.service.CouponService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coupon createCoupon(@Valid @RequestBody Coupon coupon) {
        return couponService.createCoupon(coupon);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Coupon getCouponById(@PathVariable Long id) {
        return couponService.getCouponById(id);
    }

    @GetMapping("/code/{code}")
    public Coupon getCouponByCode(@PathVariable String code) {
        return couponService.getCouponByCode(code);
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody Coupon coupon) {
        return couponService.updateCoupon(id, coupon);
    }

    @DeleteMapping("/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return "Coupon deleted successfully";
    }
}