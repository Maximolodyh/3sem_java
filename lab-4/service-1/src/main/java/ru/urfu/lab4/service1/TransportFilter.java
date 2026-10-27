package ru.urfu.lab4.service1;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TransportFilter extends OncePerRequestFilter {
  public static final String CORRELATION = "correlationId", RECEIVED = "receivedAt";

  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String id = req.getHeader("X-Correlation-ID");
    if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
    req.setAttribute(CORRELATION, id);
    req.setAttribute(RECEIVED, OffsetDateTime.now(ZoneOffset.UTC).toString());
    res.setHeader("X-Correlation-ID", id);
    chain.doFilter(req, res);
  }
}
