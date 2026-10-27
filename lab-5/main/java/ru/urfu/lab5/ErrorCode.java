package ru.urfu.lab5;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
  EMPTY(""),
  ValidationException("ValidationException"),
  UnknownException("UnknownException"),
  UnsupportedException("UnsupportedException"),
  NotManagerException("NotManagerException");

  private final String value;

  ErrorCode(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
