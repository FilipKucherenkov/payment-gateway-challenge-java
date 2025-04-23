package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;

public record PostPaymentResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("order_id") UUID orderId,
    @JsonProperty("status") PaymentStatus status,
    @JsonProperty("card_number_last_four") int cardNumberLastFour,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency_code") CurrencyCode currencyCode,
    @JsonProperty("amount") long amount
) {

  public static PostPaymentResponse from(Payment payment) {
    return new PostPaymentResponse(
        payment.getId(),
        payment.getOrderId(),
        payment.getStatus(),
        extractLastFourDigits(payment.getCardNumber()),
        extractExpiryMonth(payment.getExpiryDate()),
        extractExpiryYear(payment.getExpiryDate()),
        payment.getCurrency(),
        payment.getAmount()
    );
  }


  @Override
  public String toString() {
    return "PostPaymentResponse{" +
        "id=" + id +
        ", orderId=" + orderId +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currencyCode='" + currencyCode + '\'' +
        ", amount=" + amount +
        '}';
  }

  private static int extractLastFourDigits(String cardNumber){
    return Integer.parseInt(cardNumber.substring(cardNumber.length() - 5));
  }

  private static int extractExpiryYear(LocalDate expiryDate) {
    return expiryDate.getYear();
  }

  private static int extractExpiryMonth(LocalDate expiryDate) {
    return expiryDate.getMonth().getValue();
  }

}
