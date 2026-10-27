package ru.urfu.lab3;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service("timeModifier")
@Order(20)
public class TimeModifier implements ModifyResponseService {
  private final DateTimeUtil dateTime;

  public TimeModifier(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  public FeedbackResponse modify(FeedbackResponse r) {
    return r.withSystemTime(dateTime.nowUtc());
  }
}
