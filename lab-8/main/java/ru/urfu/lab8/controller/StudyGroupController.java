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
import ru.urfu.lab8.dto.GroupRequest;
import ru.urfu.lab8.dto.GroupResponse;
import ru.urfu.lab8.service.StudyGroupService;

@RestController
@RequestMapping("/api/groups")
public class StudyGroupController {
  private final StudyGroupService service;

  public StudyGroupController(StudyGroupService service) {
    this.service = service;
  }

  @GetMapping
  public List<GroupResponse> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public GroupResponse one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest r) {
    GroupResponse g = service.create(r);
    return ResponseEntity.created(URI.create("/api/groups/" + g.id())).body(g);
  }

  @PutMapping("/{id}")
  public GroupResponse update(@PathVariable long id, @Valid @RequestBody GroupRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
