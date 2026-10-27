package ru.urfu.lab5;

public class NotManagerException extends RuntimeException {
  public NotManagerException() {
    super("Квартальная премия доступна только менеджерам");
  }
}
