
package com.mano.dispatch.repository;

import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.enums.OrderStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryOrderRepository
        extends JpaRepository<DeliveryOrder, Long> {

    List<DeliveryOrder> findByStatus(OrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT o FROM DeliveryOrder o
            WHERE o.id = :orderId
            """)
    Optional<DeliveryOrder> findByIdWithLock(
            @Param("orderId") Long orderId);
}
