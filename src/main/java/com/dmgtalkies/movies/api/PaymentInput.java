package com.dmgtalkies.movies.api;
import jakarta.validation.constraints.NotBlank;
public record PaymentInput(@NotBlank String paymentToken) {}
