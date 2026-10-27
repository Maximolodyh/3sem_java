package ru.urfu.lab3;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {
  EMPTY(""),
  VALIDATION("Ошибка валидации"),
  UNKNOWN("Произошла непредвиденная ошибка"),
  UNSUPPORTED("Не поддерживаемая ошибка");
  private final String value;

  ErrorMessage(String v) {
    value = v;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
