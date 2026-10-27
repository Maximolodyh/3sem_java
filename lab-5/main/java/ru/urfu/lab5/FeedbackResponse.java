package ru.urfu.lab5;

import java.math.BigDecimal;

/** Ответ feedback, обогащённый премиями. */
public record FeedbackResponse(
    /** Идентификатор запроса. */
    String uid,
    /** Новый идентификатор операции. */
    String operationUid,
    /** Время формирования ответа UTC. */
    String systemTime,
    /** success или failed. */
    ResponseCode code,
    /** Машиночитаемый код ошибки. */
    ErrorCode errorCode,
    /** Безопасное описание ошибки. */
    ErrorMessage errorMessage,
    /** Годовая премия, округлённая до копеек. */
    BigDecimal annualBonus,
    /** Квартальная премия менеджера или null. */
    BigDecimal quarterlyBonus) {

  public FeedbackResponse withOperationUid(String value) {
    return new FeedbackResponse(
        uid, value, systemTime, code, errorCode, errorMessage, annualBonus, quarterlyBonus);
  }

  public FeedbackResponse withSystemTime(String value) {
    return new FeedbackResponse(
        uid, operationUid, value, code, errorCode, errorMessage, annualBonus, quarterlyBonus);
  }
}
