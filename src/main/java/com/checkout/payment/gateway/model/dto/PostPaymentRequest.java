package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;


public record PostPaymentRequest(
    @JsonProperty("order_id") UUID orderId,
    @JsonProperty("card_number") String card_number,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency_code") CurrencyCode currencyCode,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements Serializable {

  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }


  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + card_number +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currencyCode='" + currencyCode + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
