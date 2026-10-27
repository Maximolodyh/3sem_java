package ru.urfu.lab5;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Полный запрос feedback и данные для расчёта премии. */
public record FeedbackRequest(
    /** Идентификатор запроса, максимум 32 символа. */
    @NotBlank @Size(max = 32) String uid,
    /** Идентификатор операции. */
    @NotBlank @Size(max = 32) String operationUid,
    /** Система-источник ERP/CRM/WMS. */
    Systems systemName,
    /** Время системы в ISO-8601 с часовым смещением. */
    @NotNull OffsetDateTime systemTime,
    /** Произвольный источник сообщения. */
    String source,
    /** Идентификатор коммуникации 1..100000. */
    @NotNull @Min(1) @Max(100000) Integer communicationId,
    /** Необязательный id шаблона. */
    Integer templateId,
    /** Необязательный код продукта. */
    Integer productCode,
    /** Необязательный SMS-код. */
    Integer smsCode,
    /** Должность сотрудника. */
    @NotNull Position position,
    /** Положительный годовой оклад. */
    @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal salary,
    /** Доля премии: 0.10 означает 10%. */
    @NotNull @DecimalMin("0") BigDecimal bonus,
    /** Отработанные дни: окончательная верхняя граница проверяется по году. */
    @NotNull @Min(1) @Max(366) Integer workDays,
    /** Календарный год. */
    @NotNull @Min(1) @Max(9999) Integer year) {}
