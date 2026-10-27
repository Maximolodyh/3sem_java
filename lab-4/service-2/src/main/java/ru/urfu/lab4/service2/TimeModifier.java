package ru.urfu.lab4.service2;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.urfu.lab4.common.FeedbackResponse;

@Service("timeModifier")
@Order(20)
public class TimeModifier implements ModifyResponseService {
  private final DateTimeUtil dateTime;

  public TimeModifier(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public FeedbackResponse modify(FeedbackResponse response) {
    return response.withSystemTime(dateTime.nowUtc().toString());
  }
}
