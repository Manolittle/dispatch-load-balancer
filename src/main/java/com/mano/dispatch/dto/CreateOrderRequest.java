package com.mano.dispatch.dto;

import com.mano.dispatch.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequest {

    @NotBlank
    private String customerName;

    @NotNull
    private Double pickupLatitude;

    public String getCustomerName() {
        return customerName;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public Double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public Double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public void setDeliveryLatitude(Double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public void setDeliveryLongitude(Double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @NotNull
    private Double pickupLongitude;

    @NotNull
    private Double deliveryLatitude;

    @NotNull
    private Double deliveryLongitude;

    @NotNull
    @Positive
    private Double weightKg;

    @NotNull
    private Priority priority;

    public CreateOrderRequest() {
    }

    // Generate getters and setters using IntelliJ
}