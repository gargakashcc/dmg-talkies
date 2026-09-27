package com.dmgtalkies.movies.api;
import jakarta.validation.constraints.*;
public record TheaterInput(@NotNull Long cityId, @NotBlank String name, String address) {}
