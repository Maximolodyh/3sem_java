package ru.urfu.lab4.service1;

import org.springframework.http.HttpStatus;

public class DownstreamException extends RuntimeException {
  private final HttpStatus status;

  public DownstreamException(HttpStatus s, String m) {
    super(m);
    status = s;
  }

  public HttpStatus status() {
    return status;
  }
}
