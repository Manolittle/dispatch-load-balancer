
package com.mano.dispatch.service;

import com.mano.dispatch.dto.CreateOrderRequest;
import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.entity.DispatchAssignment;
import com.mano.dispatch.entity.Vehicle;
import com.mano.dispatch.enums.OrderStatus;
import com.mano.dispatch.enums.VehicleStatus;
import com.mano.dispatch.exception.ResourceNotFoundException;
import com.mano.dispatch.exception.BadRequestException;
import com.mano.dispatch.repository.DeliveryOrderRepository;
import com.mano.dispatch.repository.DispatchAssignmentRepository;
import com.mano.dispatch.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final DispatchAssignmentRepository assignmentRepository;
    private final VehicleRepository vehicleRepository;

    public DeliveryOrderService(
            DeliveryOrderRepository deliveryOrderRepository,
            DispatchAssignmentRepository assignmentRepository,
            VehicleRepository vehicleRepository) {

        this.deliveryOrderRepository = deliveryOrderRepository;
        this.assignmentRepository = assignmentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public DeliveryOrder createOrder(CreateOrderRequest request) {

        DeliveryOrder order = new DeliveryOrder();

        order.setCustomerName(request.getCustomerName());
        order.setPickupLatitude(request.getPickupLatitude());
        order.setPickupLongitude(request.getPickupLongitude());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setWeightKg(request.getWeightKg());
        order.setPriority(request.getPriority());
        order.setStatus(OrderStatus.PENDING);

        return deliveryOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<DeliveryOrder> getAllOrders() {
        return deliveryOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DeliveryOrder getOrderById(Long id) {
        return deliveryOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery order not found with ID: " + id));
    }

    @Transactional
    public DeliveryOrder completeDelivery(Long id) {

        DeliveryOrder order = deliveryOrderRepository
                .findByIdWithLock(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery order not found with ID: " + id));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new BadRequestException(
                    "Only assigned orders can be marked as delivered");
        }

        DispatchAssignment assignment = assignmentRepository
                .findByDeliveryOrder_Id(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assignment not found for order: " + id));

        Vehicle vehicle = vehicleRepository
                .findByIdWithLock(assignment.getVehicle().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assigned vehicle not found"));

        order.setStatus(OrderStatus.DELIVERED);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        deliveryOrderRepository.save(order);
        vehicleRepository.save(vehicle);

        return order;
    }
}
