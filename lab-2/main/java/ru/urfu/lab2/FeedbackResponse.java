package ru.urfu.lab2;

public record FeedbackResponse(
    String uid,
    String operationUid,
    String systemTime,
    String code,
    String errorCode,
    String errorMessage) {}
