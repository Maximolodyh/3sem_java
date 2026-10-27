package ru.urfu.lab8.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.urfu.lab8.dto.GroupForm;
import ru.urfu.lab8.dto.GroupResponse;
import ru.urfu.lab8.dto.StudentForm;
import ru.urfu.lab8.dto.StudentResponse;
import ru.urfu.lab8.exception.ConflictException;
import ru.urfu.lab8.exception.NotFoundException;
import ru.urfu.lab8.service.StudentService;
import ru.urfu.lab8.service.StudyGroupService;

@Controller
public class WebController {
  private final StudentService students;
  private final StudyGroupService groups;

  public WebController(StudentService students, StudyGroupService groups) {
    this.students = students;
    this.groups = groups;
  }

  @GetMapping("/")
  String home() {
    return "home";
  }

  @GetMapping("/list")
  String legacyList() {
    return "redirect:/students";
  }

  @GetMapping("/addStudentForm")
  String legacyAdd() {
    return "redirect:/students/new";
  }

  @GetMapping("/students")
  String students(Model m) {
    m.addAttribute("students", students.findAll());
    return "students";
  }

  @GetMapping("/students/new")
  String newStudent(Model m) {
    m.addAttribute("studentForm", new StudentForm());
    m.addAttribute("groups", groups.findAll());
    m.addAttribute("action", "/students");
    return "student-form";
  }

  @PostMapping("/students")
  String createStudent(@Valid @ModelAttribute StudentForm studentForm, BindingResult b, Model m) {
    if (!b.hasErrors())
      try {
        students.create(studentForm.toRequest());
        return "redirect:/students";
      } catch (NotFoundException e) {
        b.rejectValue("groupId", "missing", e.getMessage());
      }
    m.addAttribute("groups", groups.findAll());
    m.addAttribute("action", "/students");
    return "student-form";
  }

  @GetMapping("/students/{id}/edit")
  String editStudent(@PathVariable long id, Model m) {
    StudentResponse s = students.find(id);
    StudentForm f = new StudentForm();
    f.setName(s.name());
    f.setSurname(s.surname());
    f.setFaculty(s.faculty());
    f.setAge(s.age());
    f.setGroupId(s.groupId());
    m.addAttribute("studentForm", f);
    m.addAttribute("groups", groups.findAll());
    m.addAttribute("action", "/students/" + id);
    return "student-form";
  }

  @PostMapping("/students/{id}")
  String updateStudent(
      @PathVariable long id,
      @Valid @ModelAttribute StudentForm studentForm,
      BindingResult b,
      Model m) {
    if (!b.hasErrors())
      try {
        students.update(id, studentForm.toRequest());
        return "redirect:/students";
      } catch (NotFoundException e) {
        if (e.getMessage().contains("группа")) b.rejectValue("groupId", "missing", e.getMessage());
        else throw e;
      }
    m.addAttribute("groups", groups.findAll());
    m.addAttribute("action", "/students/" + id);
    return "student-form";
  }

  @PostMapping("/students/{id}/delete")
  String deleteStudent(@PathVariable long id) {
    students.delete(id);
    return "redirect:/students";
  }

  @GetMapping("/groups")
  String groups(Model m, @RequestParam(required = false) String error) {
    m.addAttribute("groups", groups.findAll());
    m.addAttribute("error", error);
    return "groups";
  }

  @GetMapping("/groups/new")
  String newGroup(Model m) {
    m.addAttribute("groupForm", new GroupForm());
    m.addAttribute("action", "/groups");
    return "group-form";
  }

  @PostMapping("/groups")
  String createGroup(@Valid @ModelAttribute GroupForm groupForm, BindingResult b, Model m) {
    m.addAttribute("action", "/groups");
    if (b.hasErrors()) return "group-form";
    try {
      groups.create(groupForm.toRequest());
      return "redirect:/groups";
    } catch (ConflictException e) {
      b.rejectValue("code", "duplicate", e.getMessage());
      return "group-form";
    }
  }

  @GetMapping("/groups/{id}/edit")
  String editGroup(@PathVariable long id, Model m) {
    GroupResponse g = groups.find(id);
    GroupForm f = new GroupForm();
    f.setCode(g.code());
    f.setTitle(g.title());
    m.addAttribute("groupForm", f);
    m.addAttribute("action", "/groups/" + id);
    return "group-form";
  }

  @PostMapping("/groups/{id}")
  String updateGroup(
      @PathVariable long id, @Valid @ModelAttribute GroupForm groupForm, BindingResult b, Model m) {
    m.addAttribute("action", "/groups/" + id);
    if (b.hasErrors()) return "group-form";
    try {
      groups.update(id, groupForm.toRequest());
      return "redirect:/groups";
    } catch (ConflictException e) {
      b.rejectValue("code", "duplicate", e.getMessage());
      return "group-form";
    }
  }

  @PostMapping("/groups/{id}/delete")
  String deleteGroup(@PathVariable long id, RedirectAttributes r) {
    try {
      groups.delete(id);
    } catch (ConflictException e) {
      r.addAttribute("error", e.getMessage());
    }
    return "redirect:/groups";
  }
}
