package ru.urfu.lab5;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {
  private final FeedbackService service;

  public FeedbackController(FeedbackService s) {
    service = s;
  }

  @PostMapping("/feedback")
  FeedbackResponse feedback(@Valid @RequestBody FeedbackRequest r) {
    return service.process(r);
  }

  @PostMapping("/bonus/quarterly")
  BigDecimal quarterly(@Valid @RequestBody FeedbackRequest r) {
    return service.quarterly(r);
  }
}
