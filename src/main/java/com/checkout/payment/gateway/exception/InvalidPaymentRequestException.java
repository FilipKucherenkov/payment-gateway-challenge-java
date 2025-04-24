package com.checkout.payment.gateway.exception;

import java.util.List;

public class InvalidPaymentRequestException extends Throwable {
  private final List<String> errors;

  public InvalidPaymentRequestException(List<String> errors) {
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }
}
