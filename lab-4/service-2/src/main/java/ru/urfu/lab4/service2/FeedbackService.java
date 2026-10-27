package ru.urfu.lab4.service2;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.urfu.lab4.common.ErrorCode;
import ru.urfu.lab4.common.ErrorMessage;
import ru.urfu.lab4.common.FeedbackRequest;
import ru.urfu.lab4.common.FeedbackResponse;
import ru.urfu.lab4.common.ResponseCode;

@Service
public class FeedbackService {
  private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
  private final List<ModifyResponseService> modifiers;

  public FeedbackService(
      @Qualifier("uuidModifier") ModifyResponseService uuidModifier,
      @Qualifier("timeModifier") ModifyResponseService timeModifier) {
    this.modifiers = List.of(uuidModifier, timeModifier);
  }

  public FeedbackResponse process(FeedbackRequest request) {
    log.info("Получен feedback uid={} operationUid={}", request.uid(), request.operationUid());
    if ("123".equals(request.uid())) throw new UnsupportedCodeException();
    FeedbackResponse response =
        new FeedbackResponse(
            request.uid(),
            request.operationUid(),
            request.systemTime().toString(),
            ResponseCode.success,
            ErrorCode.EMPTY,
            ErrorMessage.EMPTY);
    for (ModifyResponseService modifier : modifiers) {
      response = modifier.modify(response);
      log.info(
          "uid={} operationUid={} ответ изменён компонентом {}",
          request.uid(),
          response.operationUid(),
          modifier.getClass().getSimpleName());
    }
    return response;
  }
}
