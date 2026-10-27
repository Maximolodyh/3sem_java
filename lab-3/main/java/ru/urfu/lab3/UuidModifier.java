package ru.urfu.lab3;

import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service("uuidModifier")
@Order(10)
public class UuidModifier implements ModifyResponseService {
  private final Supplier<UUID> supplier;

  public UuidModifier(Supplier<UUID> supplier) {
    this.supplier = supplier;
  }

  public FeedbackResponse modify(FeedbackResponse r) {
    return r.withOperationUid(supplier.get().toString());
  }
}
