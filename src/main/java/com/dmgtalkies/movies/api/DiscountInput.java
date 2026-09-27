package com.dmgtalkies.movies.api;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record DiscountInput(
    @NotBlank String code, @Min(1) @Max(100) int percent, LocalDateTime expiresAt) {}
