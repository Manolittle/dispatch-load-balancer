
package com.mano.dispatch.service;

import com.mano.dispatch.dto.CreateVehicleRequest;
import com.mano.dispatch.dto.VehicleResponse;
import com.mano.dispatch.entity.Vehicle;
import com.mano.dispatch.enums.VehicleStatus;
import com.mano.dispatch.repository.VehicleRepository;
import com.mano.dispatch.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setCapacityKg(request.getCapacityKg());
        vehicle.setCurrentLatitude(request.getCurrentLatitude());
        vehicle.setCurrentLongitude(request.getCurrentLongitude());
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return convertToResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with ID: " + id));

        return convertToResponse(vehicle);
    }


    @Transactional
    public VehicleResponse updateVehicleStatus(
            Long vehicleId, VehicleStatus status) {

        Vehicle vehicle = vehicleRepository.findByIdWithLock(vehicleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found with ID: " + vehicleId));

        vehicle.setStatus(status);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return convertToResponse(updatedVehicle);
    }

    private VehicleResponse convertToResponse(Vehicle vehicle) {

        VehicleResponse response = new VehicleResponse();

        response.setId(vehicle.getId());
        response.setRegistrationNumber(vehicle.getRegistrationNumber());
        response.setCapacityKg(vehicle.getCapacityKg());
        response.setCurrentLatitude(vehicle.getCurrentLatitude());
        response.setCurrentLongitude(vehicle.getCurrentLongitude());
        response.setStatus(vehicle.getStatus());

        return response;
    }
}
