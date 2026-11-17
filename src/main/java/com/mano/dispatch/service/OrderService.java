package com.mano.dispatch.service;

import com.mano.dispatch.dto.CreateOrderRequest;
import com.mano.dispatch.dto.OrderResponse;
import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.enums.OrderStatus;
import com.mano.dispatch.repository.DeliveryOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final DeliveryOrderRepository orderRepository;

    public OrderService(DeliveryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        DeliveryOrder order = new DeliveryOrder();

        order.setCustomerName(request.getCustomerName());
        order.setPickupLatitude(request.getPickupLatitude());
        order.setPickupLongitude(request.getPickupLongitude());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setWeightKg(request.getWeightKg());
        order.setPriority(request.getPriority());
        order.setStatus(OrderStatus.PENDING);

        DeliveryOrder savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private OrderResponse convertToResponse(DeliveryOrder order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setPickupLatitude(order.getPickupLatitude());
        response.setPickupLongitude(order.getPickupLongitude());
        response.setDeliveryLatitude(order.getDeliveryLatitude());
        response.setDeliveryLongitude(order.getDeliveryLongitude());
        response.setWeightKg(order.getWeightKg());
        response.setPriority(order.getPriority());
        response.setStatus(order.getStatus());

        return response;
    }
}