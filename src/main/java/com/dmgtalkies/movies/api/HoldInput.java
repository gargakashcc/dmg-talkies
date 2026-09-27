package com.dmgtalkies.movies.api;
import jakarta.validation.constraints.*;
import java.util.Set;
public record HoldInput(@NotNull Long showId, @NotEmpty Set<@NotBlank String> seats, String discountCode) {}
