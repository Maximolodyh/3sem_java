package ru.urfu.lab3;

import java.util.List;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
  private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
  private final List<ModifyResponseService> modifiers;

  public FeedbackService(
      @Qualifier("uuidModifier") ModifyResponseService uuid,
      @Qualifier("timeModifier") ModifyResponseService time) {
    this.modifiers = List.of(uuid, time);
  }

  public FeedbackResponse process(FeedbackRequest r) {
    log.info("Получен feedback uid={}", r.uid());
    if ("123".equals(r.uid())) throw new UnsupportedCodeException();
    FeedbackResponse response =
        new FeedbackResponse(
            r.uid(),
            r.operationUid(),
            r.systemTime().toString(),
            ResponseCode.success,
            ErrorCode.EMPTY,
            ErrorMessage.EMPTY);
    for (ModifyResponseService m : modifiers) {
      response = m.modify(response);
      log.info(
          "uid={} operationUid={} ответ изменён компонентом {}",
          r.uid(),
          response.operationUid(),
          m.getClass().getSimpleName());
    }
    log.info("Обработка feedback завершена uid={}", r.uid());
    return response;
  }
}
