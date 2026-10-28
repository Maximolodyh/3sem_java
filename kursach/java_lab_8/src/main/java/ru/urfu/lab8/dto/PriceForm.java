package ru.urfu.lab8.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PriceForm {
  @NotNull
  private Long bookId;
  @NotNull
  private Long storeId;
  @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
  private BigDecimal price;

  public Long getBookId() { return bookId; }
  public void setBookId(Long value) { bookId = value; }
  public Long getStoreId() { return storeId; }
  public void setStoreId(Long value) { storeId = value; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal value) { price = value; }
}
