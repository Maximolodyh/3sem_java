package ru.urfu.lab6.controller;

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
import ru.urfu.lab6.dto.SubjectRequest;
import ru.urfu.lab6.dto.SubjectResponse;
import ru.urfu.lab6.service.SubjectService;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
  private final SubjectService service;

  public SubjectController(SubjectService service) {
    this.service = service;
  }

  @GetMapping
  public List<SubjectResponse> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public SubjectResponse one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest r) {
    SubjectResponse s = service.create(r);
    return ResponseEntity.created(URI.create("/api/subjects/" + s.id())).body(s);
  }

  @PutMapping("/{id}")
  public SubjectResponse update(@PathVariable long id, @Valid @RequestBody SubjectRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
