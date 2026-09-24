package com.fleetflow.repository;

import com.fleetflow.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByCustomerId(Long customerId);

    Optional<Trip> findByIdempotencyKey(String idempotencyKey);
}