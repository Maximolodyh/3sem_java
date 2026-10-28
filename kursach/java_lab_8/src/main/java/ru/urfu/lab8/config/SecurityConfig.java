package ru.urfu.lab8.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  FilterRegistrationBean<DatabaseAccountRefreshFilter> refreshRegistration(
      DatabaseAccountRefreshFilter filter) {
    FilterRegistrationBean<DatabaseAccountRefreshFilter> bean =
        new FilterRegistrationBean<>(filter);
    bean.setEnabled(false);
    return bean;
  }

  @Bean
  SecurityFilterChain filters(HttpSecurity http, DatabaseAccountRefreshFilter refresh,
      ru.urfu.lab8.service.ActionLogService audit)
      throws Exception {
    return http.authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/",
                        "/register",
                        "/login",
                        "/css/**",
                        "/error",
                        "/actuator/health",
                        "/actuator/info")
                    .permitAll()
                    .requestMatchers("/users", "/users/**", "/logs", "/actuator/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/books", "/books/**", "/bookstores", "/bookstores/**", "/prices", "/prices/**")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/books/new", "/books/*/edit",
                        "/bookstores/new", "/bookstores/*/edit", "/prices/new", "/prices/*/edit")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/books", "/books/**", "/bookstores", "/bookstores/**", "/prices", "/prices/**", "/calculator", "/about")
                    .hasAnyRole("READ_ONLY", "USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/**")
                    .hasAnyRole("USER", "EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students/*/delete", "/groups/*/delete")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students/**", "/groups/**")
                    .hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/students/new",
                        "/students/*/edit",
                        "/groups/new",
                        "/groups/*/edit")
                    .hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers("/students/**", "/groups/**", "/list", "/addStudentForm")
                    .hasAnyRole("USER", "EDITOR", "ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            f ->
                f.loginPage("/login")
                    .successHandler((req, res, authentication) -> {
                      audit.record(authentication.getName(), "Вход в приложение");
                      res.sendRedirect(req.getContextPath() + "/books");
                    })
                    .failureUrl("/login?error")
                    .permitAll())
        .logout(
            l ->
                l.logoutUrl("/logout")
                    .addLogoutHandler((req, res, authentication) -> {
                      if (authentication != null) audit.record(authentication.getName(), "Выход из приложения");
                    })
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        .exceptionHandling(
            e ->
                e.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**"))
                    .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**"))))
        .sessionManagement(
            s ->
                s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation(x -> x.migrateSession()))
        .addFilterAfter(refresh, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
