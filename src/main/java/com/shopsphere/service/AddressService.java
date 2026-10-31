package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.Address;

public interface AddressService {

    Address addAddress(Long customerId, Address address);

    List<Address> getAddressesByCustomerId(Long customerId);

    Address updateAddress(Long addressId, Address address);

    void deleteAddress(Long addressId);
}