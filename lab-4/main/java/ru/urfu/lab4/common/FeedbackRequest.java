package ru.urfu.lab4.common;

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
    Integer smsCode) {
  public FeedbackRequest withSource(String s) {
    return new FeedbackRequest(
        uid,
        operationUid,
        systemName,
        systemTime,
        s,
        communicationId,
        templateId,
        productCode,
        smsCode);
  }
}
