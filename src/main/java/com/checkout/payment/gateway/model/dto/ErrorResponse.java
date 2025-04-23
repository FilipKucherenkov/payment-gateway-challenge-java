package com.checkout.payment.gateway.model.dto;

public record ErrorResponse(String message) {

  @Override
  public String toString() {
    return "ErrorResponse{" +
        "message='" + message + '\'' +
        '}';
  }
}
