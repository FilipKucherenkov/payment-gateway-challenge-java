package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.exception.InvalidPaymentRequestException;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidationAspectTest {

  @InjectMocks
  private ValidationAspect underTest;

  @Test
  void when_valid_request_is_validated_no_exception_occurs() {
    PostPaymentRequest request = new PostPaymentRequest(
        UUID.randomUUID(),
        "4111111111111111",
        12,
        YearMonth.now().plusYears(1).getYear(),
        CurrencyCode.USD,
        100,
        "123"
    );

    assertDoesNotThrow(() -> underTest.validatePaymentRequest(request));
  }

  @Test
  void when_invalid_request_is_validated_all_errors_are_thrown() {
    PostPaymentRequest invalidRequest = new PostPaymentRequest(
        null,
        "abc123",
        13,
        2020,
        null,
        0,
        "12a"
    );

    InvalidPaymentRequestException exception = assertThrows(
        InvalidPaymentRequestException.class,
        () -> underTest.validatePaymentRequest(invalidRequest)
    );

    assertThat(exception.getErrors()).containsExactlyInAnyOrder(
        "Card number must be 14-19 digits long and numeric",
        "Expiry month must be between 1 and 12",
        "OrderId is required",
        "Currency is required",
        "Amount must be greater than 0",
        "CVV must be 3 or 4 digit numeric"
    );
  }

  @Test
  void when_card_is_expired_then_expiry_date_error_is_thrown() {
    PostPaymentRequest expiredCard = new PostPaymentRequest(
        UUID.randomUUID(),
        "4111111111111111",
        1,
        YearMonth.now().minusYears(1).getYear(),
        CurrencyCode.USD,
        100,
        "123"
    );

    InvalidPaymentRequestException exception = assertThrows(
        InvalidPaymentRequestException.class,
        () -> underTest.validatePaymentRequest(expiredCard)
    );

    assertThat(exception.getErrors()).contains("Card expiry date must be in the future");
  }
}