package ru.urfu.lab4.service2;

import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.urfu.lab4.common.FeedbackResponse;

@Service("uuidModifier")
@Order(10)
public class UuidModifier implements ModifyResponseService {
  private final Supplier<UUID> supplier;

  public UuidModifier(Supplier<UUID> supplier) {
    this.supplier = supplier;
  }

  @Override
  public FeedbackResponse modify(FeedbackResponse response) {
    return response.withOperationUid(supplier.get().toString());
  }
}
