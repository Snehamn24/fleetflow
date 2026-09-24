package com.fleetflow.service;

import com.fleetflow.entity.Driver;
import com.fleetflow.repository.DriverRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver createDriver(Driver driver) {

        if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Driver with this license number already exists"
            );
        }

        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Driver not found with id: " + id
                        ));
    }
}