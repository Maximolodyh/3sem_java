package ru.urfu.lab4.service2;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.lab4.common.FeedbackRequest;
import ru.urfu.lab4.common.FeedbackResponse;

@RestController
public class FeedbackController {
  private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);
  private final FeedbackService service;
  private final DateTimeUtil dateTime;

  public FeedbackController(FeedbackService service, DateTimeUtil dateTime) {
    this.service = service;
    this.dateTime = dateTime;
  }

  @PostMapping("/feedback")
  FeedbackResponse feedback(
      @Valid @RequestBody FeedbackRequest request,
      @RequestHeader("X-Correlation-ID") String correlation,
      @RequestHeader("X-Service-1-Received-At") String receivedAt) {
    long delta = Duration.between(OffsetDateTime.parse(receivedAt), dateTime.nowUtc()).toMillis();
    log.info(
        "correlationId={} uid={} source={} wall-clock delta={} ms (negative indicates clock skew)",
        correlation,
        request.uid(),
        request.source(),
        delta);
    return service.process(request);
  }
}
