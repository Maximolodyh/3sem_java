package ru.urfu.lab3;

import java.util.stream.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private final DateTimeUtil dateTime;

  public ApiExceptionHandler(DateTimeUtil dateTime) {
    this.dateTime = dateTime;
  }

  private FeedbackResponse error(ErrorCode c, ErrorMessage m) {
    return new FeedbackResponse("", "", dateTime.nowUtc(), ResponseCode.failed, c, m);
  }

  @ExceptionHandler(UnsupportedCodeException.class)
  ResponseEntity<FeedbackResponse> unsupported() {
    log.error("Неподдерживаемый uid");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.UnsupportedException, ErrorMessage.UNSUPPORTED));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<FeedbackResponse> invalid(MethodArgumentNotValidException e) {
    String fields =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining(","));
    log.error("Ошибка валидации полей: {}", fields);
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<FeedbackResponse> binding() {
    log.error("Ошибка привязки JSON");
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<FeedbackResponse> unknown(Exception e) {
    if (e instanceof ErrorResponse frameworkError) {
      return ResponseEntity.status(frameworkError.getStatusCode()).build();
    }
    log.error("Непредвиденная ошибка", e);
    return ResponseEntity.internalServerError()
        .body(error(ErrorCode.UnknownException, ErrorMessage.UNKNOWN));
  }
}
