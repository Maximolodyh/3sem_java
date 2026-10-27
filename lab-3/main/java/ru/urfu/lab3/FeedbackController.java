package ru.urfu.lab3;

import jakarta.validation.Valid;
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
}
