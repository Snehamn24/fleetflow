package com.fleetflow.service;

import com.fleetflow.entity.Payment;
import com.fleetflow.entity.PaymentStatus;
import com.fleetflow.entity.Trip;
import com.fleetflow.entity.TripStatus;
import com.fleetflow.repository.PaymentRepository;
import com.fleetflow.repository.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            TripRepository tripRepository) {

        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public Payment processPayment(Long tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Trip not found with id: " + tripId
                        ));

        if (trip.getStatus() == TripStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment cannot be processed for a cancelled trip"
            );
        }

        if (trip.getFareAmount() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Fare has not been calculated for this trip"
            );
        }

        Optional<Payment> existingPayment =
                paymentRepository.findByTripId(tripId);

        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        Payment payment = new Payment();

        payment.setTrip(trip);
        payment.setAmount(trip.getFareAmount());

        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setTransactionReference(
                "PAY-" + UUID.randomUUID()
        );

        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found with id: " + id
                        ));
    }

    public Payment getPaymentByTripId(Long tripId) {

        return paymentRepository.findByTripId(tripId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Payment not found for trip: " + tripId
                        ));
    }
}