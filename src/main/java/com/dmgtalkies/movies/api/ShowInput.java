package com.dmgtalkies.movies.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowInput(
    @NotNull Long theaterId,
    @NotBlank String title,
    @NotNull LocalDateTime startsAt,
    @NotNull @DecimalMin("0") BigDecimal regularPrice,
    @NotNull @DecimalMin("0") BigDecimal premiumPrice,
    @Min(0) int weekendMarkupPercent) {}
