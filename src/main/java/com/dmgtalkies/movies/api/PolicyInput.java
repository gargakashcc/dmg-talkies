package com.dmgtalkies.movies.api;

import jakarta.validation.constraints.*;

public record PolicyInput(@Min(0) int minimumHours, @Min(0) @Max(100) int refundPercent) {}
