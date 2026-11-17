
package com.mano.dispatch.service;

import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.entity.Vehicle;
import com.mano.dispatch.entity.DispatchAssignment;

import com.mano.dispatch.enums.OrderStatus;
import com.mano.dispatch.enums.VehicleStatus;
import com.mano.dispatch.enums.Priority;

import com.mano.dispatch.exception.BadRequestException;
import com.mano.dispatch.exception.ResourceNotFoundException;

import com.mano.dispatch.repository.DeliveryOrderRepository;
import com.mano.dispatch.repository.VehicleRepository;
import com.mano.dispatch.repository.DispatchAssignmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private DeliveryOrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DispatchAssignmentRepository assignmentRepository;

    @InjectMocks
    private DispatchService dispatchService;

    private DeliveryOrder order;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {

        order = new DeliveryOrder();
        order.setId(1L);
        order.setCustomerName("ABC Logistics");
        order.setPickupLatitude(13.0827);
        order.setPickupLongitude(80.2707);
        order.setDeliveryLatitude(13.0674);
        order.setDeliveryLongitude(80.2376);
        order.setWeightKg(300.0);
        order.setPriority(Priority.HIGH);
        order.setStatus(OrderStatus.PENDING);

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setRegistrationNumber("TN02AB5678");
        vehicle.setCapacityKg(500.0);
        vehicle.setCurrentLatitude(13.0800);
        vehicle.setCurrentLongitude(80.2650);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
    }

    @Test
    void assignOrderSuccessfully() {

        when(orderRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(order));

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE))
                .thenReturn(List.of(vehicle));

        when(vehicleRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(vehicle));

        when(assignmentRepository.save(any(DispatchAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DispatchAssignment result = dispatchService.assignOrder(1L);

        assertNotNull(result);
        assertEquals(order, result.getDeliveryOrder());
        assertEquals(vehicle, result.getVehicle());
        assertTrue(result.getDistanceKm() >= 0);

        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertEquals(VehicleStatus.BUSY, vehicle.getStatus());

        verify(orderRepository).findByIdWithLock(1L);
        verify(orderRepository).save(order);
        verify(vehicleRepository).findByIdWithLock(1L);
        verify(vehicleRepository).save(vehicle);
        verify(assignmentRepository).save(any(DispatchAssignment.class));
    }

    @Test
    void assignOrderShouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findByIdWithLock(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dispatchService.assignOrder(1L)
        );

        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(assignmentRepository);
    }

    @Test
    void assignOrderShouldThrowExceptionWhenOrderIsNotPending() {

        order.setStatus(OrderStatus.ASSIGNED);

        when(orderRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                BadRequestException.class,
                () -> dispatchService.assignOrder(1L)
        );

        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(assignmentRepository);
    }

    @Test
    void assignOrderShouldThrowExceptionWhenNoSuitableVehicleExists() {

        when(orderRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(order));

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE))
                .thenReturn(List.of());

        assertThrows(
                BadRequestException.class,
                () -> dispatchService.assignOrder(1L)
        );

        assertEquals(OrderStatus.PENDING, order.getStatus());

        verify(assignmentRepository, never())
                .save(any(DispatchAssignment.class));
    }

    @Test
    void assignOrderShouldSkipVehicleThatIsNoLongerAvailable() {

        when(orderRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(order));

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE))
                .thenReturn(List.of(vehicle));

        // Simulate the vehicle becoming BUSY before its lock is acquired.
        vehicle.setStatus(VehicleStatus.BUSY);

        when(vehicleRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(
                BadRequestException.class,
                () -> dispatchService.assignOrder(1L)
        );

        assertEquals(OrderStatus.PENDING, order.getStatus());

        verify(assignmentRepository, never())
                .save(any(DispatchAssignment.class));
    }
}
