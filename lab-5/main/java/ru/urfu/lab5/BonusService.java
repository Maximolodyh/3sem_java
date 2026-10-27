package ru.urfu.lab5;

import java.math.*;
import java.time.Year;
import org.springframework.stereotype.Service;

@Service
public class BonusService {
  private BigDecimal numerator(
      Position position, BigDecimal salary, BigDecimal bonus, int workDays, int year) {
    int days = Year.isLeap(year) ? 366 : 365;
    if (workDays > days) throw new IllegalArgumentException("workDays превышает число дней года");
    return salary
        .multiply(bonus)
        .multiply(BigDecimal.valueOf(days))
        .multiply(position.coefficient());
  }

  public BigDecimal annual(Position p, BigDecimal s, BigDecimal b, int w, int y) {
    return numerator(p, s, b, w, y).divide(BigDecimal.valueOf(w), 2, RoundingMode.HALF_UP);
  }

  public BigDecimal quarterly(Position p, BigDecimal s, BigDecimal b, int w, int y) {
    if (!p.isManager()) throw new NotManagerException();
    return numerator(p, s, b, w, y)
        .divide(BigDecimal.valueOf((long) w * 4), 2, RoundingMode.HALF_UP);
  }
}
