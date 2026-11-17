
package com.mano.dispatch.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.mano.dispatch.dto.CreateOrderRequest;
import com.mano.dispatch.entity.DeliveryOrder;
import com.mano.dispatch.service.DeliveryOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Delivery order management APIs")
public class OrderController {

    private final DeliveryOrderService deliveryOrderService;

    public OrderController(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }
    @Operation(
            summary = "Create a delivery order",
            description = "Creates a new delivery order with PENDING status."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryOrder createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return deliveryOrderService.createOrder(request);
    }
    @Operation(summary = "Get all delivery orders")
    @GetMapping
    public List<DeliveryOrder> getAllOrders() {
        return deliveryOrderService.getAllOrders();
    }
    @Operation(summary = "Get delivery order by ID")
    @GetMapping("/{id}")
    public DeliveryOrder getOrderById(@PathVariable Long id) {
        return deliveryOrderService.getOrderById(id);
    }
    @Operation(
            summary = "Complete delivery",
            description = "Marks an assigned order as delivered and releases its vehicle."
    )
    @PutMapping("/{id}/complete")
    public DeliveryOrder completeDelivery(@PathVariable Long id) {
        return deliveryOrderService.completeDelivery(id);
    }
}
