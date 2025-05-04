package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GetPaymentResponseTest {

  private static UUID PAYMENT_ID = UUID.randomUUID();
  private static UUID ORDER_ID = UUID.randomUUID();
  private static String CARD_NUMBER = "4111111111111115";
  private static int EXPIRY_YEAR = 2140;
  private static int EXPIRY_MONTH = 12;
  private static LocalDate EXPIRY_DATE = LocalDate.of(EXPIRY_YEAR, EXPIRY_MONTH, 1);


  @Test
  void from_should_map_payment_to_response_correctly() {

    Payment payment = new Payment();
    payment.setId(PAYMENT_ID);
    payment.setOrderId(ORDER_ID);
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setCardNumber(CARD_NUMBER);
    payment.setExpiryDate(EXPIRY_DATE);
    payment.setCurrency(CurrencyCode.USD);
    payment.setAmount(1000L);

    GetPaymentResponse response = GetPaymentResponse.from(payment);

    assertThat(response.id()).isEqualTo(PAYMENT_ID);
    assertThat(response.orderId()).isEqualTo(ORDER_ID);
    assertThat(response.status()).isEqualTo(PaymentStatus.AUTHORIZED);
    assertThat(response.cardNumberLastFour()).isEqualTo(1115);
    assertThat(response.expiryMonth()).isEqualTo(EXPIRY_MONTH);
    assertThat(response.expiryYear()).isEqualTo(EXPIRY_YEAR);
    assertThat(response.currency()).isEqualTo(CurrencyCode.USD);
    assertThat(response.amount()).isEqualTo(1000L);
  }
}
