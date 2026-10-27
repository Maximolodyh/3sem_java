package ru.urfu.lab1;

import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {
  private final ValueStore store;

  public HelloController(ValueStore store) {
    this.store = store;
  }

  @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
  public String hello(@RequestParam(defaultValue = "World") String name) {
    return "Hello, " + name + "!";
  }

  @GetMapping("/update-array")
  public List<String> updateArray(@RequestParam String s) {
    return store.addList(s);
  }

  @GetMapping("/show-array")
  public List<String> showArray() {
    return store.list();
  }

  @GetMapping("/update-map")
  public Map<Integer, String> updateMap(@RequestParam String s) {
    return store.addMap(s);
  }

  @GetMapping("/show-map")
  public Map<Integer, String> showMap() {
    return store.map();
  }

  @GetMapping(
      value = {"/show-all-lenght", "/show-all-length"},
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String sizes() {
    return store.sizes();
  }
}
