package com.dmgtalkies.movies.api;
import jakarta.validation.constraints.NotBlank;
public record CityInput(@NotBlank String name) {}
