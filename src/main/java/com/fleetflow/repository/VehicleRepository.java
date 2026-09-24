package com.fleetflow.repository;

import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.entity.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Vehicle> findFirstByTypeAndStatusOrderByIdAsc(
            VehicleType type,
            VehicleStatus status
    );
}