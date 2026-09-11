package com.archive.archive.repositories;


import java.util.List;

import com.archive.archive.models.RequestStatus;
import com.archive.archive.models.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.Order;

public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findByRequestTypeAndRequestStatus(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<Order> findByRequestTypeAndRequestStatusOrderByDoc_Name(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<Order> findByRequestTypeAndRequestStatusOrderByDoc_Folder(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<Order> findByRequestTypeAndRequestStatusOrderByEmployee_Email(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<Order> findByRequestTypeAndRequestStatusOrderByOrderDate(
            RequestType requestType,
            RequestStatus requestStatus
    );
}