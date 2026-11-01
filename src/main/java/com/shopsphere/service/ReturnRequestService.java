package com.shopsphere.service;

import java.util.List;

import com.shopsphere.entity.ReturnRequest;

public interface ReturnRequestService {

    ReturnRequest createReturnRequest(Long orderId, String reason);

    List<ReturnRequest> getAllReturnRequests();

    ReturnRequest getReturnRequestById(Long id);

    List<ReturnRequest> getReturnRequestsByOrderId(Long orderId);

    ReturnRequest updateReturnRequestStatus(Long id, String status);

    void deleteReturnRequest(Long id);
}