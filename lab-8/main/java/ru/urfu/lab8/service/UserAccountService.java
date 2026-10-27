package ru.urfu.lab8.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab8.dto.RegistrationForm;
import ru.urfu.lab8.entity.Role;
import ru.urfu.lab8.entity.UserAccount;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.repository.RoleRepository;
import ru.urfu.lab8.repository.UserAccountRepository;

@Service
@Transactional(readOnly = true)
public class UserAccountService implements UserDetailsService {
  private final UserAccountRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;

  public UserAccountService(
      UserAccountRepository users, RoleRepository roles, PasswordEncoder encoder) {
    this.users = users;
    this.roles = roles;
    this.encoder = encoder;
  }

  @Transactional
  public UserAccount register(RegistrationForm f) {
    String email = normalize(f.getEmail());
    if (users.existsByEmailIgnoreCase(email))
      throw new ConflictException("Адрес электронной почты уже зарегистрирован");
    UserAccount u =
        new UserAccount(
            f.getFirstName().trim(),
            f.getLastName().trim(),
            email,
            encoder.encode(f.getPassword()));
    u.getRoles().add(role("ROLE_USER"));
    try {
      return users.saveAndFlush(u);
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("Адрес электронной почты уже зарегистрирован");
    }
  }

  @Transactional
  public UserAccount bootstrap(
      String first, String last, String email, String password, Set<String> roleNames) {
    String normalized = normalize(email);
    UserAccount u =
        users
            .findByEmailIgnoreCase(normalized)
            .orElseGet(() -> new UserAccount(first, last, normalized, encoder.encode(password)));
    u.getRoles().clear();
    roleNames.stream()
        .map(n -> n.startsWith("ROLE_") ? n : "ROLE_" + n)
        .map(this::role)
        .forEach(u.getRoles()::add);
    return users.save(u);
  }

  public List<UserAccount> all() {
    return users.findAll();
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    UserAccount u =
        users
            .findByEmailIgnoreCase(normalize(email))
            .orElseThrow(() -> new UsernameNotFoundException("Неверные учётные данные"));
    return toPrincipal(u);
  }

  public UserDetails toPrincipal(UserAccount u) {
    return User.withUsername(u.getEmail())
        .password(u.getPasswordHash())
        .authorities(
            u.getRoles().stream().map(Role::getName).map(SimpleGrantedAuthority::new).toList())
        .disabled(!u.isActive())
        .build();
  }

  private Role role(String name) {
    return roles.findByName(name).orElseGet(() -> roles.save(new Role(name)));
  }

  private String normalize(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
