package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.entity.Address;
import com.shopsphere.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(
            @RequestParam Long customerId,
            @Valid @RequestBody Address address) {

        return new ResponseEntity<>(
                addressService.addAddress(customerId, address),
                HttpStatus.CREATED);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Address>> getAddresses(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                addressService.getAddressesByCustomerId(customerId));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody Address address) {

        return ResponseEntity.ok(
                addressService.updateAddress(addressId, address));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.ok("Address deleted successfully");
    }
}