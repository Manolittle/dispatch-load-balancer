
package com.mano.dispatch.controller;

import com.mano.dispatch.entity.DispatchAssignment;
import com.mano.dispatch.service.DispatchService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(
        name = "Dispatch",
        description = "Order and vehicle assignment APIs"
)
@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }
    @Operation(
            summary = "Manually assign an order",
            description = "Assigns a pending order to a suitable available vehicle based on capacity and distance."
    )
    @PostMapping("/assign/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchAssignment assignOrder(
            @PathVariable Long orderId) {

        return dispatchService.assignOrder(orderId);
    }

    @Operation(
            summary = "Automatically assign pending orders",
            description = "Processes pending orders by priority and assigns suitable available vehicles."
     )
    @PostMapping("/auto-assign")
    public List<DispatchAssignment> autoAssignOrders() {

        return dispatchService.autoAssignOrders();
    }
}
