package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.model.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class AcquiringBankRequestTest {

  private static UUID PAYMENT_ID = UUID.randomUUID();
  private static UUID ORDER_ID = UUID.randomUUID();
  private static String CARD_NUMBER = "4111111111111115";
  private static int EXPIRY_YEAR = 2140;
  private static int EXPIRY_MONTH = 12;
  private static String CVV = "123";

  @Test
  void from_should_map_payment_to_response_correctly() {

    PostPaymentRequest payment = new PostPaymentRequest(
        ORDER_ID,
        CARD_NUMBER,
        EXPIRY_MONTH,
        EXPIRY_YEAR,
        CurrencyCode.USD,
        1000L,
        CVV
    );

    AcquiringBankRequest response = AcquiringBankRequest.from(payment);

    assertThat(response.cvv()).isEqualTo(CVV);
    assertThat(response.cardNumber()).isEqualTo(CARD_NUMBER);
    assertThat(response.currencyCode()).isEqualTo(CurrencyCode.USD);
    assertThat(response.amount()).isEqualTo(1000L);
  }
}

