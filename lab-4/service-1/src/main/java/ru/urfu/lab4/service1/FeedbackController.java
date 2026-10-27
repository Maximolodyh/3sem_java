package ru.urfu.lab4.service1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.urfu.lab4.common.*;

@RestController
public class FeedbackController {
  private final ModifyRequestService modifier;
  private final DownstreamClient downstream;

  public FeedbackController(ModifyRequestService m, DownstreamClient d) {
    modifier = m;
    downstream = d;
  }

  @PostMapping("/feedback")
  FeedbackResponse feedback(@Valid @RequestBody FeedbackRequest r, HttpServletRequest http) {
    if ("123".equals(r.uid())) throw new UnsupportedCodeException();
    return downstream.send(
        modifier.modify(r),
        (String) http.getAttribute(TransportFilter.CORRELATION),
        (String) http.getAttribute(TransportFilter.RECEIVED));
  }
}
