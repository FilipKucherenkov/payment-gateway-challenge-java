package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.PageNotFoundException;
import com.checkout.payment.gateway.exception.PaymentConflictException;
import com.checkout.payment.gateway.model.dto.GetPaymentResponse;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.dto.PostPaymentResponse;
import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.validation.ValidatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankIntegrationService acquiringBankIntegrationService;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      AcquiringBankIntegrationService acquiringBankIntegrationService) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankIntegrationService = acquiringBankIntegrationService;
  }

  public GetPaymentResponse getPaymentById(UUID id) {
    LOGGER.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.findById(id).map(GetPaymentResponse::from)
        .orElseThrow(() -> new PageNotFoundException("Invalid Payment ID"));
  }

  @ValidatePaymentRequest
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest){
    paymentsRepository.findByOrderId(paymentRequest.orderId())
        .ifPresent(p -> {
          throw new PaymentConflictException("Payment already processed");
        });
    boolean isAuthorized = acquiringBankIntegrationService.initiatePayment(paymentRequest);

    Payment payment = paymentsRepository.save(Payment.from(paymentRequest, isAuthorized));
    return PostPaymentResponse.from(payment);
  }

}
