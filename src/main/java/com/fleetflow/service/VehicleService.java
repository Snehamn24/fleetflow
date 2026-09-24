package com.fleetflow.service;

import com.fleetflow.dto.request.CreateVehicleRequest;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.entity.VehicleStatus;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository) {

        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    public Vehicle createVehicle(CreateVehicleRequest request) {

        if (vehicleRepository.existsByRegistrationNumber(
                request.getRegistrationNumber())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle with this registration number already exists"
            );
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Driver not found with id: " + request.getDriverId()
                        ));

        Vehicle vehicle = new Vehicle();

        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setType(request.getType());
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setDriver(driver);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Vehicle not found with id: " + id
                        ));
    }
}