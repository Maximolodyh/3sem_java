package ru.urfu.lab3;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record FeedbackRequest(
    @NotBlank @Size(max = 32) String uid,
    @NotBlank @Size(max = 32) String operationUid,
    Systems systemName,
    @NotNull OffsetDateTime systemTime,
    String source,
    @NotNull @Min(1) @Max(100000) Integer communicationId,
    Integer templateId,
    Integer productCode,
    Integer smsCode) {}
