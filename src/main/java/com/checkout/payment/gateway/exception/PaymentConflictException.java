package com.checkout.payment.gateway.exception;

public class PaymentConflictException extends RuntimeException{
  public PaymentConflictException(String message) {
    super(message);
  }
}
