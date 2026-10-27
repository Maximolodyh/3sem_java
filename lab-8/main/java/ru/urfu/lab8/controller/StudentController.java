package ru.urfu.lab8.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.lab8.dto.StudentRequest;
import ru.urfu.lab8.dto.StudentResponse;
import ru.urfu.lab8.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {
  private final StudentService service;

  public StudentController(StudentService service) {
    this.service = service;
  }

  @GetMapping
  public List<StudentResponse> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public StudentResponse one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest r) {
    StudentResponse s = service.create(r);
    return ResponseEntity.created(URI.create("/api/students/" + s.id())).body(s);
  }

  @PutMapping("/{id}")
  public StudentResponse update(@PathVariable long id, @Valid @RequestBody StudentRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
