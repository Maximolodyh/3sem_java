package ru.urfu.lab8.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.lab8.entity.UserAction;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
  List<UserAction> findTop100ByOrderByDateActionsDescIdDesc();
}
