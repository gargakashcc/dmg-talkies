package com.dmgtalkies.movies.api;

import jakarta.validation.constraints.NotBlank;

public record SeatInput(@NotBlank String label, boolean premium) {}
