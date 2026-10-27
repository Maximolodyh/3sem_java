package ru.urfu.lab4.service1;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.urfu.lab4.common.FeedbackRequest;
import ru.urfu.lab4.common.FeedbackResponse;

@Component
public class RestDownstreamClient implements DownstreamClient {
  private static final Logger log = LoggerFactory.getLogger(RestDownstreamClient.class);
  private final RestClient client;

  @Autowired
  public RestDownstreamClient(
      RestClient.Builder builder,
      @Value("${service2.url}") String url,
      @Value("${service2.connect-timeout}") Duration connect,
      @Value("${service2.read-timeout}") Duration read) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(connect);
    factory.setReadTimeout(read);
    this.client = builder.baseUrl(url).requestFactory(factory).build();
  }

  RestDownstreamClient(RestClient client) {
    this.client = client;
  }

  @Override
  public FeedbackResponse send(FeedbackRequest request, String correlation, String receivedAt) {
    long start = System.nanoTime();
    try {
      FeedbackResponse response =
          client
              .post()
              .uri("/feedback")
              .header("X-Correlation-ID", correlation)
              .header("X-Service-1-Received-At", receivedAt)
              .contentType(MediaType.APPLICATION_JSON)
              .body(request)
              .retrieve()
              .body(FeedbackResponse.class);
      validateResponse(response);
      return response;
    } catch (ResourceAccessException error) {
      if (hasTimeoutCause(error))
        throw new DownstreamException(HttpStatus.GATEWAY_TIMEOUT, "Таймаут service-2");
      throw new DownstreamException(HttpStatus.BAD_GATEWAY, "Service-2 недоступен");
    } catch (RestClientException error) {
      throw new DownstreamException(HttpStatus.BAD_GATEWAY, "Ошибка ответа service-2");
    } finally {
      log.info("RTT service-1/service-2={} ms", (System.nanoTime() - start) / 1_000_000);
    }
  }

  private void validateResponse(FeedbackResponse response) {
    if (response == null
        || response.uid() == null
        || response.uid().isBlank()
        || response.operationUid() == null
        || response.operationUid().isBlank()
        || response.systemTime() == null
        || response.code() == null
        || response.errorCode() == null
        || response.errorMessage() == null) {
      throw new DownstreamException(HttpStatus.BAD_GATEWAY, "Некорректная схема ответа service-2");
    }
    try {
      OffsetDateTime.parse(response.systemTime());
    } catch (DateTimeParseException exception) {
      throw new DownstreamException(HttpStatus.BAD_GATEWAY, "Некорректное время ответа service-2");
    }
  }

  private boolean hasTimeoutCause(Throwable error) {
    for (Throwable current = error; current != null; current = current.getCause()) {
      if (current instanceof SocketTimeoutException) return true;
    }
    return false;
  }
}
