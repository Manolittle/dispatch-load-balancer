
package com.mano.dispatch.controller;

import com.mano.dispatch.enums.VehicleStatus;
import com.mano.dispatch.dto.CreateVehicleRequest;
import com.mano.dispatch.dto.VehicleResponse;
import com.mano.dispatch.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle management APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Create a new vehicle")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {

        return vehicleService.createVehicle(request);
    }

    @Operation(summary = "Get vehicle by ID")
    @GetMapping("/{id}")
    public VehicleResponse getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @Operation(summary = "Get all vehicles")
    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Operation(
            summary = "Update vehicle status",
            description = "Changes vehicle status to AVAILABLE, BUSY or MAINTENANCE."
    )
    @PutMapping("/{id}/status")
    public VehicleResponse updateVehicleStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {

        return vehicleService.updateVehicleStatus(id, status);
    }
}
