package ru.urfu.lab2;

import java.time.*;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
  private final Clock clock;

  public FeedbackService() {
    this(Clock.systemUTC());
  }

  FeedbackService(Clock clock) {
    this.clock = clock;
  }

  public FeedbackResponse process(FeedbackRequest r) {
    if ("123".equals(r.uid())) throw new UnsupportedCodeException();
    return new FeedbackResponse(
        r.uid(), r.operationUid(), OffsetDateTime.now(clock).toString(), "success", "", "");
  }
}
