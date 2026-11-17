package com.mano.dispatch.repository;

import com.mano.dispatch.entity.DispatchAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface DispatchAssignmentRepository
        extends JpaRepository<DispatchAssignment, Long> {
    Optional<DispatchAssignment> findByDeliveryOrder_Id(Long orderId);
    List<DispatchAssignment> findByVehicle_Id(Long vehicleId);
}