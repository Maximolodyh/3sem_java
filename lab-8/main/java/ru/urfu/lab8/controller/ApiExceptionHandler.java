package ru.urfu.lab8.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urfu.lab8.dto.ApiError;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.exception.NotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiError> notFound(NotFoundException e) {
    return response(404, e.getMessage(), Map.of());
  }

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiError> conflict(ConflictException e) {
    return response(409, e.getMessage(), Map.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
    Map<String, String> m = new LinkedHashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(x -> m.putIfAbsent(x.getField(), x.getDefaultMessage()));
    return response(400, "Ошибка валидации", m);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> malformed() {
    return response(400, "Некорректный JSON", Map.of());
  }

  private ResponseEntity<ApiError> response(int status, String message, Map<String, String> v) {
    return ResponseEntity.status(status).body(new ApiError(Instant.now(), status, message, v));
  }
}
