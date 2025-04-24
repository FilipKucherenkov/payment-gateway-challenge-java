package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public record AcquiringBankRequest(
    @JsonProperty("card_number") String cardNumber,
    @JsonProperty("expiry_date") String expiryDate,
    @JsonProperty("currency_code") CurrencyCode currencyCode,
    @JsonProperty("amount") long amount,
    @JsonProperty("cvv") String cvv
) implements Serializable {

  public static AcquiringBankRequest from(PostPaymentRequest paymentRequest) {
    return new AcquiringBankRequest(
        paymentRequest.card_number(),
        paymentRequest.getExpiryDate(),
        paymentRequest.currencyCode(),
        paymentRequest.amount(),
        paymentRequest.cvv()
    );
  }

  @Override
  public String toString() {
    return "AcquiringBankRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryDate=" + expiryDate +
        ", currencyCode=" + currencyCode +
        ", cvv='" + cvv + '\'' +
        ", amount=" + amount +
        '}';
  }

}
