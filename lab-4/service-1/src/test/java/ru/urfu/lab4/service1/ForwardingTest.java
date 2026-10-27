package ru.urfu.lab4.service1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.net.SocketTimeoutException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import ru.urfu.lab4.common.FeedbackRequest;
import ru.urfu.lab4.common.FeedbackResponse;
import ru.urfu.lab4.common.Systems;

class ForwardingTest {
  private MockRestServiceServer server;
  private RestDownstreamClient client;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://service-2.test");
    server = MockRestServiceServer.bindTo(builder).build();
    client = new RestDownstreamClient(builder.build());
  }

  private FeedbackRequest request() {
    return new FeedbackRequest(
        "u",
        "o",
        Systems.ERP,
        OffsetDateTime.parse("2026-10-26T00:00+05:00"),
        "client",
        1,
        null,
        null,
        null);
  }

  @Test
  void modifierCreatesNewRequest() {
    assertEquals("service-1", new SourceModifier().modify(request()).source());
    assertEquals("client", request().source());
  }

  @Test
  void typedSuccessIsReturned() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(
            withSuccess(
                "{\"uid\":\"u\",\"operationUid\":\"new\",\"systemTime\":\"2026-10-25T19:00:00Z\",\"code\":\"success\",\"errorCode\":\"\",\"errorMessage\":\"\"}",
                MediaType.APPLICATION_JSON));
    FeedbackResponse response = client.send(request(), "c1", "2026-10-25T19:00:00Z");
    assertEquals("new", response.operationUid());
    server.verify();
  }

  @Test
  void upstreamStatusBecomesBadGateway() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.BAD_GATEWAY, error.status());
  }

  @Test
  void malformedJsonBecomesBadGateway() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(withSuccess("{", MediaType.APPLICATION_JSON));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.BAD_GATEWAY, error.status());
  }

  @Test
  void missingResponseFieldBecomesBadGateway() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(
            withSuccess(
                "{\"uid\":\"u\",\"operationUid\":\"new\",\"systemTime\":\"2026-10-25T19:00:00Z\",\"code\":\"success\",\"errorCode\":\"\"}",
                MediaType.APPLICATION_JSON));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.BAD_GATEWAY, error.status());
  }

  @Test
  void invalidEnumBecomesBadGateway() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(
            withSuccess(
                "{\"uid\":\"u\",\"operationUid\":\"new\",\"systemTime\":\"2026-10-25T19:00:00Z\",\"code\":\"maybe\",\"errorCode\":\"\",\"errorMessage\":\"\"}",
                MediaType.APPLICATION_JSON));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.BAD_GATEWAY, error.status());
  }

  @Test
  void invalidSystemTimeBecomesBadGateway() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(
            withSuccess(
                "{\"uid\":\"u\",\"operationUid\":\"new\",\"systemTime\":\"not-a-time\",\"code\":\"success\",\"errorCode\":\"\",\"errorMessage\":\"\"}",
                MediaType.APPLICATION_JSON));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.BAD_GATEWAY, error.status());
  }

  @Test
  void timeoutBecomesGatewayTimeout() {
    server
        .expect(requestTo("http://service-2.test/feedback"))
        .andRespond(withException(new SocketTimeoutException("read timed out")));
    DownstreamException error =
        assertThrows(
            DownstreamException.class, () -> client.send(request(), "c1", "2026-10-25T19:00:00Z"));
    assertEquals(HttpStatus.GATEWAY_TIMEOUT, error.status());
  }
}
