package ru.urfu.lab5;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service("timeModifier")
@Order(20)
public class TimeModifier implements ModifyResponseService {
  private final DateTimeUtil dateTime;

  public TimeModifier(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public FeedbackResponse modify(FeedbackResponse response) {
    return response.withSystemTime(dateTime.nowUtc());
  }
}
