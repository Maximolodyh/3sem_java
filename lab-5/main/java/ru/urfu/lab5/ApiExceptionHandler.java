package ru.urfu.lab5;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private final DateTimeUtil dateTime;

  public ApiExceptionHandler(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  private FeedbackResponse error(ErrorCode code, ErrorMessage message) {
    return new FeedbackResponse(
        "", "", dateTime.nowUtc(), ResponseCode.failed, code, message, null, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<FeedbackResponse> invalid(MethodArgumentNotValidException error) {
    String fields =
        error.getBindingResult().getFieldErrors().stream()
            .map(field -> field.getField() + ": " + field.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining("; "));
    log.error("Ошибка валидации: {}", fields);
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<FeedbackResponse> invalidDomain(IllegalArgumentException error) {
    log.error("Ошибка доменного ограничения: {}", error.getMessage());
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(NotManagerException.class)
  ResponseEntity<FeedbackResponse> manager() {
    log.error("Запрошена квартальная премия для неменеджерской позиции");
    return ResponseEntity.unprocessableEntity()
        .body(error(ErrorCode.NotManagerException, ErrorMessage.NOT_MANAGER));
  }

  @ExceptionHandler(UnsupportedCodeException.class)
  ResponseEntity<FeedbackResponse> unsupported() {
    log.error("Неподдерживаемый uid");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.UnsupportedException, ErrorMessage.UNSUPPORTED));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<FeedbackResponse> malformed() {
    log.error("Ошибка привязки JSON");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<FeedbackResponse> unknown(Exception error) {
    if (error instanceof ErrorResponse frameworkError) {
      return ResponseEntity.status(frameworkError.getStatusCode()).build();
    }
    log.error("Непредвиденная ошибка обработки feedback", error);
    return ResponseEntity.internalServerError()
        .body(error(ErrorCode.UnknownException, ErrorMessage.UNKNOWN));
  }
}
