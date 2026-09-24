package com.fleetflow.controller;

import com.fleetflow.dto.request.CreateTripRequest;
import com.fleetflow.entity.Trip;
import com.fleetflow.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trip createTrip(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateTripRequest request) {

        return tripService.createTrip(
                request,
                idempotencyKey
        );
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}")
    public Trip getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<Trip> getTripsByCustomer(
            @PathVariable Long customerId) {

        return tripService.getTripsByCustomer(customerId);
    }

    @PatchMapping("/{id}/start")
    public Trip startTrip(@PathVariable Long id) {
        return tripService.startTrip(id);
    }

    @PatchMapping("/{id}/complete")
    public Trip completeTrip(@PathVariable Long id) {
        return tripService.completeTrip(id);
    }

    @PatchMapping("/{id}/cancel")
    public Trip cancelTrip(@PathVariable Long id) {
        return tripService.cancelTrip(id);
    }
}