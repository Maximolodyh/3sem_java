package ru.urfu.lab4.service1;

import ru.urfu.lab4.common.*;

public interface DownstreamClient {
  FeedbackResponse send(FeedbackRequest request, String correlation, String receivedAt);
}
