package com.shopsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.entity.Address;
import com.shopsphere.entity.Customer;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.CustomerRepository;
import com.shopsphere.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Address addAddress(Long customerId, Address address) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId));

        address.setCustomer(customer);

        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressesByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return addressRepository.findByCustomer_Id(customerId);
    }

    @Override
    public Address updateAddress(Long addressId, Address updatedAddress) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId));

        address.setAddressLine(updatedAddress.getAddressLine());
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setPincode(updatedAddress.getPincode());
        address.setCountry(updatedAddress.getCountry());

        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId));

        addressRepository.delete(address);
    }
}