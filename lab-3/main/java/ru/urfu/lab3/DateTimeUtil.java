package ru.urfu.lab3;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {
  private final Clock clock;

  public DateTimeUtil(Clock clock) {
    this.clock = clock;
  }

  public String nowUtc() {
    return OffsetDateTime.now(clock).toString();
  }
}
