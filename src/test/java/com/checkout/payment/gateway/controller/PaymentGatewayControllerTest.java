package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  private static final UUID PAYMENT_ID = UUID.fromString("8f51e0a5-b5e7-4fcd-b1b1-1edcb128afc4");
  private static final UUID ORDER_ID = UUID.fromString("2e4c9cf2-6936-469e-a438-4a4c49a13a68");
  private static final String CARD_NUMBER = "12345678901234";
  private static final String CVV = "123";
  private static final int EXPIRY_MONTH = 12;
  private static final int EXPIRY_YEAR = 2150;
  private static final LocalDate EXPIRY_DATE = LocalDate.of(EXPIRY_YEAR, EXPIRY_MONTH, 1);
  private static final long AMOUNT = 10L;

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    Payment payment = aPayment(
        PAYMENT_ID,
        ORDER_ID,
        PaymentStatus.AUTHORIZED,
        CARD_NUMBER,
        CVV,
        EXPIRY_DATE,
        CurrencyCode.USD,
        AMOUNT
    );

    paymentsRepository.save(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + PAYMENT_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(PAYMENT_ID.toString()))
        .andExpect(jsonPath("$.order_id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.card_number_last_four").value("1234"))
        .andExpect(jsonPath("$.expiry_month").value(EXPIRY_MONTH))
        .andExpect(jsonPath("$.expiry_year").value(EXPIRY_YEAR))
        .andExpect(jsonPath("$.currency_code").value(CurrencyCode.USD.getCode()))
        .andExpect(jsonPath("$.amount").value(AMOUNT));
  }

  private Payment aPayment(UUID id, UUID orderId, PaymentStatus paymentStatus, String cardNumber, String cvv, LocalDate expiryDate, CurrencyCode currencyCode, long amount) {
    Payment payment = new Payment();
    payment.setId(id);
    payment.setOrderId(orderId);
    payment.setStatus(paymentStatus);
    payment.setCardNumber(cardNumber);
    payment.setExpiryDate(expiryDate);
    payment.setCurrency(currencyCode);
    payment.setAmount(amount);
    payment.setCvv(cvv);
    return payment;
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }
}
