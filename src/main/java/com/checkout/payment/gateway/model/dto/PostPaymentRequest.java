package com.checkout.payment.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public record PostPaymentRequest(
    @JsonProperty("order_id") String orderId,
    @JsonProperty("card_number_last_four") int cardNumberLastFour,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currencyCode") String currency,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") int cvv
) implements Serializable {

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currencyCode='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
