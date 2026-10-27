package ru.urfu.lab4.common;

public record FeedbackResponse(
    String uid,
    String operationUid,
    String systemTime,
    ResponseCode code,
    ErrorCode errorCode,
    ErrorMessage errorMessage) {
  public FeedbackResponse withOperationUid(String value) {
    return new FeedbackResponse(uid, value, systemTime, code, errorCode, errorMessage);
  }

  public FeedbackResponse withSystemTime(String value) {
    return new FeedbackResponse(uid, operationUid, value, code, errorCode, errorMessage);
  }
}
