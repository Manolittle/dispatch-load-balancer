
package com.mano.dispatch.service;

import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.entity.Vehicle;
import com.mano.dispatch.entity.DispatchAssignment;

import com.mano.dispatch.enums.OrderStatus;
import com.mano.dispatch.enums.VehicleStatus;
import com.mano.dispatch.enums.Priority;

import com.mano.dispatch.exception.ResourceNotFoundException;
import com.mano.dispatch.exception.BadRequestException;

import com.mano.dispatch.repository.DeliveryOrderRepository;
import com.mano.dispatch.repository.VehicleRepository;
import com.mano.dispatch.repository.DispatchAssignmentRepository;

import com.mano.dispatch.util.DistanceCalculator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DispatchService {

    private final DeliveryOrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchAssignmentRepository assignmentRepository;

    public DispatchService(
            DeliveryOrderRepository orderRepository,
            VehicleRepository vehicleRepository,
            DispatchAssignmentRepository assignmentRepository) {

        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // MANUAL ORDER ASSIGNMENT

    @Transactional
    public DispatchAssignment assignOrder(Long orderId) {

        // Lock the order before checking its status.
        DeliveryOrder order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not pending");
        }

        Vehicle selectedVehicle = findSuitableVehicle(order);

        if (selectedVehicle == null) {
            throw new BadRequestException(
                    "No suitable available vehicle found");
        }

        double distance = DistanceCalculator.calculateDistance(
                selectedVehicle.getCurrentLatitude(),
                selectedVehicle.getCurrentLongitude(),
                order.getPickupLatitude(),
                order.getPickupLongitude()
        );

        return saveAssignment(order, selectedVehicle, distance);
    }

    // AUTOMATIC ORDER ASSIGNMENT

    @Transactional
    public List<DispatchAssignment> autoAssignOrders() {

        // Get pending orders to determine processing order.
        List<DeliveryOrder> pendingOrders =
                orderRepository.findByStatus(OrderStatus.PENDING);

        // HIGH priority first, then MEDIUM, then LOW.
        List<DeliveryOrder> sortedOrders = pendingOrders.stream()
                .sorted(Comparator.comparingInt(order -> {
                    if (order.getPriority() == Priority.HIGH) {
                        return 1;
                    } else if (order.getPriority() == Priority.MEDIUM) {
                        return 2;
                    } else {
                        return 3;
                    }
                }))
                .toList();

        List<DispatchAssignment> assignments = new ArrayList<>();

        for (DeliveryOrder pendingOrder : sortedOrders) {

            // Lock and re-fetch each order.
            DeliveryOrder order = orderRepository
                    .findByIdWithLock(pendingOrder.getId())
                    .orElse(null);

            // Another request may have already assigned this order.
            if (order == null || order.getStatus() != OrderStatus.PENDING) {
                continue;
            }

            // Find and lock a suitable available vehicle.
            Vehicle selectedVehicle = findSuitableVehicle(order);

            // Leave the order pending if no vehicle is suitable.
            if (selectedVehicle == null) {
                continue;
            }

            double distance = DistanceCalculator.calculateDistance(
                    selectedVehicle.getCurrentLatitude(),
                    selectedVehicle.getCurrentLongitude(),
                    order.getPickupLatitude(),
                    order.getPickupLongitude()
            );

            DispatchAssignment assignment =
                    saveAssignment(order, selectedVehicle, distance);

            assignments.add(assignment);
        }

        return assignments;
    }

    // FIND NEAREST SUITABLE VEHICLE

    private Vehicle findSuitableVehicle(DeliveryOrder order) {

        List<Vehicle> availableVehicles =
                vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        List<Vehicle> sortedVehicles = availableVehicles.stream()
                .filter(vehicle ->
                        vehicle.getCapacityKg() >= order.getWeightKg())
                .sorted(Comparator.comparingDouble(vehicle ->
                        DistanceCalculator.calculateDistance(
                                vehicle.getCurrentLatitude(),
                                vehicle.getCurrentLongitude(),
                                order.getPickupLatitude(),
                                order.getPickupLongitude()
                        )))
                .toList();

        for (Vehicle candidate : sortedVehicles) {

            // Lock the vehicle before assigning it.
            Vehicle lockedVehicle = vehicleRepository
                    .findByIdWithLock(candidate.getId())
                    .orElse(null);

            if (lockedVehicle == null) {
                continue;
            }

            // Recheck status and capacity after acquiring the lock.
            if (lockedVehicle.getStatus() == VehicleStatus.AVAILABLE
                    && lockedVehicle.getCapacityKg() >= order.getWeightKg()) {

                return lockedVehicle;
            }
        }

        return null;
    }

    // SAVE ASSIGNMENT AND UPDATE STATUSES

    private DispatchAssignment saveAssignment(
            DeliveryOrder order,
            Vehicle vehicle,
            double distance) {

        DispatchAssignment assignment = new DispatchAssignment();

        assignment.setDeliveryOrder(order);
        assignment.setVehicle(vehicle);
        assignment.setDistanceKm(distance);

        order.setStatus(OrderStatus.ASSIGNED);
        vehicle.setStatus(VehicleStatus.BUSY);

        orderRepository.save(order);
        vehicleRepository.save(vehicle);

        return assignmentRepository.save(assignment);
    }
}
