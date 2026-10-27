package ru.urfu.lab8.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
    @NotBlank @Pattern(regexp = "[A-Z0-9-]{2,12}") String code,
    @NotBlank @Size(max = 80) String name,
    @Min(1) @Max(1000) int hours) {}
