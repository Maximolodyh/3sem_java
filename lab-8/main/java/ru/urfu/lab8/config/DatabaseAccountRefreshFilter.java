package ru.urfu.lab8.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.urfu.lab8.service.UserAccountService;

@Component
public class DatabaseAccountRefreshFilter extends OncePerRequestFilter {
  private final UserAccountService users;

  public DatabaseAccountRefreshFilter(UserAccountService users) {
    this.users = users;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    var current = SecurityContextHolder.getContext().getAuthentication();
    if (current != null
        && current.isAuthenticated()
        && !(current.getPrincipal() instanceof String)) {
      try {
        UserDetails fresh = users.loadUserByUsername(current.getName());
        if (!fresh.isEnabled()) throw new UsernameNotFoundException("inactive");
        var replacement =
            UsernamePasswordAuthenticationToken.authenticated(fresh, null, fresh.getAuthorities());
        replacement.setDetails(current.getDetails());
        SecurityContextHolder.getContext().setAuthentication(replacement);
      } catch (UsernameNotFoundException e) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(req, res);
  }
}
