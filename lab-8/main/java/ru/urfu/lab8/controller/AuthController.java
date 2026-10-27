package ru.urfu.lab8.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.urfu.lab8.dto.RegistrationForm;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.service.UserAccountService;

@Controller
public class AuthController {
  private final UserAccountService users;

  public AuthController(UserAccountService users) {
    this.users = users;
  }

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/register")
  String registerForm(Model m) {
    m.addAttribute("registrationForm", new RegistrationForm());
    return "register";
  }

  @PostMapping("/register")
  String register(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult b) {
    if (b.hasErrors()) return "register";
    try {
      users.register(registrationForm);
      return "redirect:/login?registered";
    } catch (ConflictException e) {
      b.rejectValue("email", "duplicate", e.getMessage());
      return "register";
    }
  }

  @GetMapping("/users")
  String users(Model m) {
    m.addAttribute("users", users.all());
    return "users";
  }
}
