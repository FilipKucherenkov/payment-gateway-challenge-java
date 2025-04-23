package com.checkout.payment.gateway.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyCode {
  GBP("GBP"),
  USD("USD"),
  EUR("EUR");

  private final String code;

  CurrencyCode(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }
}
