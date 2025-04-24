package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.enums.PaymentStatus;
import java.util.List;

public record ValidationErrorResponse(
    PaymentStatus status,
    List<String> errors
)
{
  @Override
  public String toString() {
    return "ErrorResponse{" +
        "status='" + status +
        ", errors='" + errors + '\'' +
        '}';
  }
}
