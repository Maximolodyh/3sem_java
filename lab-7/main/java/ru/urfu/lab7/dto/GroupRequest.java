package ru.urfu.lab7.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GroupRequest(
    @NotBlank @Pattern(regexp = "[A-ZА-Я0-9-]{2,20}") String code,
    @NotBlank @Size(max = 80) String title) {}
