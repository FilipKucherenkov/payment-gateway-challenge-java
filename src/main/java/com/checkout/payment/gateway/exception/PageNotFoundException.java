package com.checkout.payment.gateway.exception;

public class PageNotFoundException extends RuntimeException {
  public PageNotFoundException(String message) {
    super(message);
  }
}
