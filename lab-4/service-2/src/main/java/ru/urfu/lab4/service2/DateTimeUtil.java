package ru.urfu.lab4.service2;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {
  private final Clock clock;

  public DateTimeUtil(Clock clock) {
    this.clock = clock;
  }

  public OffsetDateTime nowUtc() {
    return OffsetDateTime.now(clock);
  }
}
