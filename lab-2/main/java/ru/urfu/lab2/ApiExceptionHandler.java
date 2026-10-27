package ru.urfu.lab2;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  private FeedbackResponse error(String code, String message) {
    return new FeedbackResponse(
        "", "", OffsetDateTime.now(ZoneOffset.UTC).toString(), "failed", code, message);
  }

  @ExceptionHandler(UnsupportedCodeException.class)
  ResponseEntity<FeedbackResponse> unsupported(UnsupportedCodeException e) {
    return ResponseEntity.badRequest().body(error("UnsupportedException", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<FeedbackResponse> validation(MethodArgumentNotValidException e) {
    String m =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining("; "));
    return ResponseEntity.badRequest().body(error("ValidationException", m));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<FeedbackResponse> malformed() {
    return ResponseEntity.badRequest()
        .body(error("ValidationException", "Некорректный JSON или тип поля"));
  }
}
