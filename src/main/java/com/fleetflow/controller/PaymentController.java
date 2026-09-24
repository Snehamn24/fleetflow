package com.fleetflow.controller;

import com.fleetflow.entity.Payment;
import com.fleetflow.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/trips/{tripId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment processPayment(
            @PathVariable Long tripId) {

        return paymentService.processPayment(tripId);
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id);
    }

    @GetMapping("/trips/{tripId}")
    public Payment getPaymentByTripId(
            @PathVariable Long tripId) {

        return paymentService.getPaymentByTripId(tripId);
    }
}