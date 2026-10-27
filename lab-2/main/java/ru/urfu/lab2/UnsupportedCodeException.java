package ru.urfu.lab2;

public class UnsupportedCodeException extends RuntimeException {
  public UnsupportedCodeException() {
    super("uid 123 не поддерживается");
  }
}
