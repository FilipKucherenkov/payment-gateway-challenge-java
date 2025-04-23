package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record GetPaymentResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("status") PaymentStatus status,
    @JsonProperty("card_number_last_four") int cardNumberLastFour,
    @JsonProperty("expiry_month")  int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currencyCode") String currency,
    @JsonProperty("amount") int amount
){

  @Override
  public String toString() {
    return "GetPaymentResponse{" +
        "id=" + id +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currencyCode='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
