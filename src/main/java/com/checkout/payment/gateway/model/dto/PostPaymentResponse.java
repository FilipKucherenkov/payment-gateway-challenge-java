package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record PostPaymentResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("order_id") UUID orderId,
    @JsonProperty("status") PaymentStatus paymentStatus,
    @JsonProperty("card_number_last_four") String lastFourCardDigits,
    @JsonProperty("expiry_month") int expiryMonth,
    @JsonProperty("expiry_year") int expiryYear,
    @JsonProperty("currency_code") CurrencyCode currencyCode,
    @JsonProperty("amount") long amount
    )
{
  @Override
  public String toString() {
    return "GetPaymentResponse{" +
        "id=" + id +
        ", orderId=" + orderId +
        ", status=" + paymentStatus +
        ", cardNumberLastFour=" + lastFourCardDigits +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currencyCode='" + currencyCode + '\'' +
        ", amount=" + amount +
        '}';
  }

  public static PostPaymentResponse from(Payment payment){
    return new PostPaymentResponse(
        payment.getId(),
        payment.getOrderId(),
        payment.getStatus(),
        payment.getCardNumber().substring(payment.getCardNumber().length()-4),
        payment.getExpiryDate().getMonthValue(),
        payment.getExpiryDate().getYear(),
        payment.getCurrency(),
        payment.getAmount()
    );
  }
}
