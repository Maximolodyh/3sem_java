package ru.urfu.lab8.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.lab8.service.ActionLogService;
import ru.urfu.lab8.service.UserAccountService;

@Controller
public class RoleManagementController {
  private final UserAccountService users;
  private final ActionLogService audit;
  public RoleManagementController(UserAccountService users, ActionLogService audit) {
    this.users = users; this.audit = audit;
  }
  @PostMapping("/users/{id}/roles")
  String grant(@PathVariable long id, @RequestParam String role,
      Authentication a, RedirectAttributes redirect) {
    if (!role.equals("USER") && !role.equals("ADMIN")) {
      redirect.addFlashAttribute("error", "Разрешено выдавать только USER или ADMIN");
      return "redirect:/users";
    }
    users.grantRole(id, role);
    audit.record(a.getName(), "Пользователю #" + id + " назначена роль " + role);
    redirect.addFlashAttribute("message", "Роль назначена");
    return "redirect:/users";
  }
}
