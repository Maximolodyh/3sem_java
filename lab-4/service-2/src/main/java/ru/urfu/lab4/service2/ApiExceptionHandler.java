package ru.urfu.lab4.service2;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urfu.lab4.common.ErrorCode;
import ru.urfu.lab4.common.ErrorMessage;
import ru.urfu.lab4.common.FeedbackResponse;
import ru.urfu.lab4.common.ResponseCode;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private final DateTimeUtil dateTime;

  public ApiExceptionHandler(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  private FeedbackResponse error(ErrorCode code, ErrorMessage message) {
    return new FeedbackResponse(
        "", "", dateTime.nowUtc().toString(), ResponseCode.failed, code, message);
  }

  @ExceptionHandler(UnsupportedCodeException.class)
  ResponseEntity<FeedbackResponse> unsupported() {
    log.error("Неподдерживаемый uid");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.UnsupportedException, ErrorMessage.UNSUPPORTED));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<FeedbackResponse> invalid(MethodArgumentNotValidException exception) {
    String fields =
        exception.getBindingResult().getFieldErrors().stream()
            .map(field -> field.getField() + ": " + field.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining("; "));
    log.error("Ошибка валидации: {}", fields);
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
  ResponseEntity<FeedbackResponse> malformed() {
    log.error("Ошибка привязки JSON или transport metadata");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<FeedbackResponse> unknown(Exception exception) {
    if (exception instanceof ErrorResponse frameworkError) {
      return ResponseEntity.status(frameworkError.getStatusCode()).build();
    }
    log.error("Непредвиденная ошибка service-2", exception);
    return ResponseEntity.internalServerError()
        .body(error(ErrorCode.UnknownException, ErrorMessage.UNKNOWN));
  }
}
