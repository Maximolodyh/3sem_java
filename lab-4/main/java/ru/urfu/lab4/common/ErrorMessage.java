package ru.urfu.lab4.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {
  EMPTY(""),
  VALIDATION("Ошибка валидации"),
  UNKNOWN("Произошла непредвиденная ошибка"),
  UNSUPPORTED("Не поддерживаемая ошибка"),
  DOWNSTREAM("Ошибка ответа service-2");

  private final String value;

  ErrorMessage(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
