package com.fleetflow.service;

import com.fleetflow.dto.request.CreateTripRequest;
import com.fleetflow.entity.*;
import com.fleetflow.repository.CustomerRepository;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public TripService(
            TripRepository tripRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository) {

        this.tripRepository = tripRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public Trip createTrip(CreateTripRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found"
                        ));

        Vehicle vehicle = vehicleRepository
                .findFirstByTypeAndStatusOrderByIdAsc(
                        request.getVehicleType(),
                        VehicleStatus.AVAILABLE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No available vehicle of requested type"
                        ));

        Driver driver = vehicle.getDriver();

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        driver.setStatus(DriverStatus.ASSIGNED);

        vehicleRepository.save(vehicle);
        driverRepository.save(driver);

        Trip trip = new Trip();

        trip.setCustomer(customer);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setPickup(request.getPickup());
        trip.setDestination(request.getDestination());
        trip.setStatus(TripStatus.CONFIRMED);
        trip.setCreatedAt(LocalDateTime.now());

        return tripRepository.save(trip);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Trip not found with id: " + id
                        ));
    }

    public List<Trip> getTripsByCustomer(Long customerId) {
        return tripRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Trip startTrip(Long id) {

        Trip trip = getTripById(id);

        if (trip.getStatus() != TripStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only a confirmed trip can be started"
            );
        }

        trip.setStatus(TripStatus.IN_PROGRESS);

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip completeTrip(Long id) {

        Trip trip = getTripById(id);

        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only an in-progress trip can be completed"
            );
        }

        trip.setStatus(TripStatus.COMPLETED);

        releaseVehicleAndDriver(trip);

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip cancelTrip(Long id) {

        Trip trip = getTripById(id);

        if (trip.getStatus() == TripStatus.COMPLETED ||
                trip.getStatus() == TripStatus.CANCELLED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Trip cannot be cancelled"
            );
        }

        trip.setStatus(TripStatus.CANCELLED);

        releaseVehicleAndDriver(trip);

        return tripRepository.save(trip);
    }

    private void releaseVehicleAndDriver(Trip trip) {

        Vehicle vehicle = trip.getVehicle();
        Driver driver = trip.getDriver();

        vehicle.setStatus(VehicleStatus.AVAILABLE);
        driver.setStatus(DriverStatus.AVAILABLE);

        vehicleRepository.save(vehicle);
        driverRepository.save(driver);
    }
}