package ru.urfu.lab4.service1;

import org.springframework.stereotype.Service;
import ru.urfu.lab4.common.FeedbackRequest;

@Service
public class SourceModifier implements ModifyRequestService {
  public FeedbackRequest modify(FeedbackRequest r) {
    return r.withSource("service-1");
  }
}
