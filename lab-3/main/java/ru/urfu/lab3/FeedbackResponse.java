package ru.urfu.lab3;

public record FeedbackResponse(
    String uid,
    String operationUid,
    String systemTime,
    ResponseCode code,
    ErrorCode errorCode,
    ErrorMessage errorMessage) {
  public FeedbackResponse withOperationUid(String v) {
    return new FeedbackResponse(uid, v, systemTime, code, errorCode, errorMessage);
  }

  public FeedbackResponse withSystemTime(String v) {
    return new FeedbackResponse(uid, operationUid, v, code, errorCode, errorMessage);
  }
}
