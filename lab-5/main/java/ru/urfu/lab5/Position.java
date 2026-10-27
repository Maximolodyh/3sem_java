package ru.urfu.lab5;

import java.math.BigDecimal;

public enum Position {
  DEV("2.2", false),
  HR("1.2", false),
  TL("2.6", true),
  PO("2.4", true),
  TPM("2.5", true),
  CTO("3.0", true);
  private final BigDecimal coefficient;
  private final boolean manager;

  Position(String c, boolean m) {
    coefficient = new BigDecimal(c);
    manager = m;
  }

  public BigDecimal coefficient() {
    return coefficient;
  }

  public boolean isManager() {
    return manager;
  }
}
