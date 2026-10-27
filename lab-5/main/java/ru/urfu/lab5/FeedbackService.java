package ru.urfu.lab5;

import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
  private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
  private final BonusService bonus;
  private final List<ModifyResponseService> modifiers;

  public FeedbackService(
      BonusService bonus,
      @Qualifier("uuidModifier") ModifyResponseService uuidModifier,
      @Qualifier("timeModifier") ModifyResponseService timeModifier) {
    this.bonus = bonus;
    this.modifiers = List.of(uuidModifier, timeModifier);
  }

  public FeedbackResponse process(FeedbackRequest request) {
    log.info("Получен feedback uid={} operationUid={}", request.uid(), request.operationUid());
    if ("123".equals(request.uid())) throw new UnsupportedCodeException();

    BigDecimal annual =
        bonus.annual(
            request.position(),
            request.salary(),
            request.bonus(),
            request.workDays(),
            request.year());
    BigDecimal quarter =
        request.position().isManager()
            ? bonus.quarterly(
                request.position(),
                request.salary(),
                request.bonus(),
                request.workDays(),
                request.year())
            : null;

    FeedbackResponse response =
        new FeedbackResponse(
            request.uid(),
            request.operationUid(),
            request.systemTime().toString(),
            ResponseCode.success,
            ErrorCode.EMPTY,
            ErrorMessage.EMPTY,
            annual,
            quarter);
    for (ModifyResponseService modifier : modifiers) {
      response = modifier.modify(response);
      log.info(
          "uid={} operationUid={} ответ изменён компонентом {}",
          request.uid(),
          response.operationUid(),
          modifier.getClass().getSimpleName());
    }
    log.info(
        "Обработка feedback завершена uid={} operationUid={}",
        request.uid(),
        response.operationUid());
    return response;
  }

  public BigDecimal quarterly(FeedbackRequest request) {
    return bonus.quarterly(
        request.position(), request.salary(), request.bonus(), request.workDays(), request.year());
  }
}
