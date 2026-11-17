
package com.mano.dispatch.repository;

import com.mano.dispatch.entity.Vehicle;
import com.mano.dispatch.enums.VehicleStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.id = :vehicleId
            """)
    Optional<Vehicle> findByIdWithLock(
            @Param("vehicleId") Long vehicleId);
}
