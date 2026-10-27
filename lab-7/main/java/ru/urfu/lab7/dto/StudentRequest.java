package ru.urfu.lab7.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(
    @NotBlank @Size(max = 15) String name,
    @NotBlank @Size(max = 25) String surname,
    @NotBlank @Size(max = 20) String faculty,
    @Min(14) @Max(120) int age,
    Long groupId) {}
