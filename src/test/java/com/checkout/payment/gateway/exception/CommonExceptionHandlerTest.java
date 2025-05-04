package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.dto.ErrorResponse;
import com.checkout.payment.gateway.model.dto.ValidationErrorResponse;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class CommonExceptionHandlerTest {

  @InjectMocks
  private CommonExceptionHandler exceptionHandler;

  @Test
  void when_PageNotFoundException_thrown_then_return_404_with_message() {
    PageNotFoundException ex = new PageNotFoundException("Invalid Payment ID");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Invalid Payment ID");
  }

  @Test
  void when_PaymentConflictException_thrown_then_return_409_with_message() {
    PaymentConflictException ex = new PaymentConflictException("Payment already processed");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex);

    assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Payment already processed");
  }

  @Test
  void when_AcquiringBankException_thrown_then_return_502_with_message() {
    AcquiringBankException ex = new AcquiringBankException("Bank error");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex);

    assertThat(response.getStatusCode()).isEqualTo(BAD_GATEWAY);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Bank error");
  }

  @Test
  void when_InvalidPaymentRequestException_thrown_then_return_400_with_errors_and_rejected_status() {
    List<String> errors = List.of("Invalid card", "Invalid CVV");
    InvalidPaymentRequestException ex = new InvalidPaymentRequestException(errors);

    ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleException(ex);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(PaymentStatus.REJECTED);
    assertThat(response.getBody().errors()).containsExactly("Invalid card", "Invalid CVV");
  }
}