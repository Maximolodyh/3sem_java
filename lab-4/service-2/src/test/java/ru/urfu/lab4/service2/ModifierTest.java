package ru.urfu.lab4.service2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.urfu.lab4.common.ErrorCode;
import ru.urfu.lab4.common.FeedbackRequest;
import ru.urfu.lab4.common.FeedbackResponse;
import ru.urfu.lab4.common.ResponseCode;
import ru.urfu.lab4.common.Systems;

class ModifierTest {
  @Test
  void fixedDependenciesProveUuidThenTimeChain() {
    UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000004");
    Clock clock = Clock.fixed(Instant.parse("2026-10-25T19:00:00Z"), ZoneOffset.UTC);
    FeedbackService service =
        new FeedbackService(
            new UuidModifier(() -> uuid), new TimeModifier(new DateTimeUtil(clock)));
    FeedbackRequest request =
        new FeedbackRequest(
            "u",
            "old",
            Systems.ERP,
            OffsetDateTime.parse("2026-10-26T00:00:00+05:00"),
            "service-1",
            1,
            null,
            null,
            null);

    FeedbackResponse response = service.process(request);

    assertEquals(uuid.toString(), response.operationUid());
    assertEquals("2026-10-25T19:00Z", response.systemTime());
    assertEquals(ResponseCode.success, response.code());
    assertEquals(ErrorCode.EMPTY, response.errorCode());
  }
}
