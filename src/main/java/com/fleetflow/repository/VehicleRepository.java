package com.fleetflow.repository;

import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Vehicle> findFirstByTypeAndStatus(
            VehicleType type,
            VehicleStatus status
    );
}