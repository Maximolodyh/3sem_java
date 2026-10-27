package ru.urfu.lab5;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {
  EMPTY(""),
  VALIDATION("Ошибка валидации"),
  UNKNOWN("Произошла непредвиденная ошибка"),
  UNSUPPORTED("Не поддерживаемая ошибка"),
  NOT_MANAGER("Квартальная премия доступна только менеджерам");

  private final String value;

  ErrorMessage(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
