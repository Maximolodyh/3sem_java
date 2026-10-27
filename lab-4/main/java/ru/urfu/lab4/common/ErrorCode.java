package ru.urfu.lab4.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
  EMPTY(""),
  ValidationException("ValidationException"),
  UnknownException("UnknownException"),
  UnsupportedException("UnsupportedException"),
  DownstreamException("DownstreamException");

  private final String value;

  ErrorCode(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
