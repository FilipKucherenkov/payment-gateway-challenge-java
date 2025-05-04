package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.PageNotFoundException;
import com.checkout.payment.gateway.exception.PaymentConflictException;
import com.checkout.payment.gateway.model.dto.GetPaymentResponse;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.dto.PostPaymentResponse;
import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentGatewayServiceTest {
  @Mock
  private PaymentsRepository paymentsRepository;

  @Mock
  private AcquiringBankIntegrationService acquiringBankIntegrationService;

  @InjectMocks
  private PaymentGatewayService underTest;

  private static final UUID PAYMENT_ID = UUID.randomUUID();;
  private static final UUID ORDER_ID = UUID.randomUUID();
  private PostPaymentRequest request;
  private Payment fakePayment;

  @BeforeEach
  void setUp() {
    request = new PostPaymentRequest(
        ORDER_ID,
        "4111111111111111",
        12,
        YearMonth.now().plusYears(1).getYear(),
        CurrencyCode.USD,
        1500,
        "123"
    );

    fakePayment = Payment.from(request, true);
    fakePayment.setId(PAYMENT_ID);
    fakePayment.setStatus(PaymentStatus.AUTHORIZED);
  }

  @Test
  void when_get_payment_by_id_exists_then_returns_response() {
    when(paymentsRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(fakePayment));

    GetPaymentResponse response = underTest.getPaymentById(PAYMENT_ID);

    assertThat(response.id()).isEqualTo(PAYMENT_ID);
    assertThat(response.amount()).isEqualTo(1500);
    verify(paymentsRepository).findById(PAYMENT_ID);
  }

  @Test
  void when_get_payment_by_id_not_found_then_throws_exception() {
    when(paymentsRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> underTest.getPaymentById(PAYMENT_ID))
        .isInstanceOf(PageNotFoundException.class)
        .hasMessageContaining("Invalid Payment ID");

    verify(paymentsRepository).findById(PAYMENT_ID);
  }

  @Test
  void when_process_payment_with_unique_order_then_persists_and_returns_response() {
    when(paymentsRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());
    when(acquiringBankIntegrationService.initiatePayment(request)).thenReturn(true);
    when(paymentsRepository.save(any(Payment.class))).thenReturn(fakePayment);

    PostPaymentResponse response = underTest.processPayment(request);

    assertThat(response.orderId()).isEqualTo(ORDER_ID);
    assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    verify(paymentsRepository).findByOrderId(ORDER_ID);
    verify(paymentsRepository).save(any(Payment.class));
  }

  @Test
  void when_process_payment_duplicate_order_then_throws_conflict_exception() {
    when(paymentsRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(fakePayment));

    assertThatThrownBy(() -> underTest.processPayment(request))
        .isInstanceOf(PaymentConflictException.class)
        .hasMessageContaining("Payment already processed");

    verify(paymentsRepository).findByOrderId(ORDER_ID);
    verify(acquiringBankIntegrationService, never()).initiatePayment(any());
    verify(paymentsRepository, never()).save(any());
  }
}
