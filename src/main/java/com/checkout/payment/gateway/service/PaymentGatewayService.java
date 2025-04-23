package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.dto.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;

  public PaymentGatewayService(PaymentsRepository paymentsRepository) {
    this.paymentsRepository = paymentsRepository;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOGGER.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.findById(id).map(PostPaymentResponse::from)
        .orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public UUID processPayment(PostPaymentRequest paymentRequest) {
    return UUID.randomUUID();
  }

}
