package ru.urfu.lab4.service2;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.web.servlet.*;

@SpringBootTest
@AutoConfigureMockMvc
class Service2Test {
  @Autowired MockMvc mvc;

  @Test
  void acceptsForwardedRequest() throws Exception {
    mvc.perform(
            post("/feedback")
                .header("X-Correlation-ID", "c1")
                .header("X-Service-1-Received-At", "2026-10-25T19:00:00Z")
                .contentType("application/json")
                .content(
                    "{\"uid\":\"u\",\"operationUid\":\"o\",\"systemTime\":\"2026-10-26T00:00:00+05:00\",\"source\":\"service-1\",\"communicationId\":1}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("success"));
  }

  @Test
  void directUnsupportedUidIsBadRequest() throws Exception {
    mvc.perform(
            post("/feedback")
                .header("X-Correlation-ID", "c2")
                .header("X-Service-1-Received-At", "2026-10-25T19:00:00Z")
                .contentType("application/json")
                .content(
                    "{\"uid\":\"123\",\"operationUid\":\"o\",\"systemTime\":\"2026-10-26T00:00:00+05:00\",\"source\":\"service-1\",\"communicationId\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("failed"))
        .andExpect(jsonPath("$.errorCode").value("UnsupportedException"));
  }
}
