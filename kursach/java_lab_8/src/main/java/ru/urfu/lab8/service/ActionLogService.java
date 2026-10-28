package ru.urfu.lab8.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.lab8.entity.UserAction;
import ru.urfu.lab8.repository.UserActionRepository;

@Service
public class ActionLogService {
  private static final Logger logger = LoggerFactory.getLogger(ActionLogService.class);
  private final UserActionRepository repository;
  public ActionLogService(UserActionRepository repository) { this.repository = repository; }
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void record(String username, String description) {
    repository.save(new UserAction(username, description));
    logger.info("Действие пользователя {}: {}", username, description);
  }
  @Transactional(readOnly = true)
  public List<UserAction> recent() { return repository.findTop100ByOrderByDateActionsDescIdDesc(); }
}
