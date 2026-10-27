package ru.urfu.lab3;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
  EMPTY(""),
  ValidationException("ValidationException"),
  UnknownException("UnknownException"),
  UnsupportedException("UnsupportedException");
  private final String value;

  ErrorCode(String v) {
    value = v;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
