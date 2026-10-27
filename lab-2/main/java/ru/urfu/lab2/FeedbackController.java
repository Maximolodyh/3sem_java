package ru.urfu.lab2;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {
  private final FeedbackService service;

  public FeedbackController(FeedbackService service) {
    this.service = service;
  }

  @PostMapping("/feedback")
  public FeedbackResponse feedback(@Valid @RequestBody FeedbackRequest request) {
    return service.process(request);
  }
}
