package com.fleetflow.service;

import com.fleetflow.entity.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareService {

    public BigDecimal calculateFare(
            VehicleType vehicleType,
            double distanceKm) {

        BigDecimal baseFare;
        BigDecimal ratePerKm;

        switch (vehicleType) {

            case CAB -> {
                baseFare = BigDecimal.valueOf(50);
                ratePerKm = BigDecimal.valueOf(15);
            }

            case AUTO -> {
                baseFare = BigDecimal.valueOf(30);
                ratePerKm = BigDecimal.valueOf(10);
            }

            case BIKE -> {
                baseFare = BigDecimal.valueOf(20);
                ratePerKm = BigDecimal.valueOf(7);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported vehicle type"
            );
        }

        BigDecimal distanceFare =
                ratePerKm.multiply(
                        BigDecimal.valueOf(distanceKm)
                );

        return baseFare
                .add(distanceFare)
                .setScale(2, RoundingMode.HALF_UP);
    }
}