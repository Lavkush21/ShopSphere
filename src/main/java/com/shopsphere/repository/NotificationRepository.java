
package com.shopsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.Notification;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    List<Notification> findByCustomer_IdAndReadFalseOrderByCreatedAtDesc(
            Long customerId);
}
