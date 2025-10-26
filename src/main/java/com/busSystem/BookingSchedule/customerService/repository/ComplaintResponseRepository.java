package com.busSystem.BookingSchedule.customerService.repository;

import com.busSystem.BookingSchedule.customerService.model.ComplaintResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintResponseRepository extends JpaRepository<ComplaintResponse, Long> {
    List<ComplaintResponse> findByComplaintId(Long complaintId);
    List<ComplaintResponse> findByCustomerServiceId(Long customerServiceId);
    List<ComplaintResponse> findByStatus(String status);
}