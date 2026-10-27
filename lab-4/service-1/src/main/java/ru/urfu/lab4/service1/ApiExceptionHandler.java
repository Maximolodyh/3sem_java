package ru.urfu.lab4.service1;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.urfu.lab4.common.ErrorCode;
import ru.urfu.lab4.common.ErrorMessage;
import ru.urfu.lab4.common.FeedbackResponse;
import ru.urfu.lab4.common.ResponseCode;

@RestControllerAdvice
public class ApiExceptionHandler {
  private FeedbackResponse error(ErrorCode code, ErrorMessage message) {
    return new FeedbackResponse(
        "", "", OffsetDateTime.now(ZoneOffset.UTC).toString(), ResponseCode.failed, code, message);
  }

  @ExceptionHandler(UnsupportedCodeException.class)
  ResponseEntity<FeedbackResponse> unsupported() {
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.UnsupportedException, ErrorMessage.UNSUPPORTED));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<FeedbackResponse> invalid() {
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<FeedbackResponse> malformed() {
    return ResponseEntity.badRequest()
        .body(error(ErrorCode.ValidationException, ErrorMessage.VALIDATION));
  }

  @ExceptionHandler(DownstreamException.class)
  ResponseEntity<FeedbackResponse> downstream(DownstreamException e) {
    return ResponseEntity.status(e.status())
        .body(error(ErrorCode.DownstreamException, ErrorMessage.DOWNSTREAM));
  }
}
