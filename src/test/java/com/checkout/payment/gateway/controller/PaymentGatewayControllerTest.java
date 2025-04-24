package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.model.dto.AcquiringBankRequest;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  private static final UUID ORDER_ID = UUID.fromString("2e4c9cf2-6936-469e-a438-4a4c49a13a68");
  private static final String CARD_NUMBER = "4111111111111115";
  private static final String CVV = "123";
  private static final int EXPIRY_MONTH = 12;
  private static final int EXPIRY_YEAR = 2150;
  private static final LocalDate EXPIRY_DATE = LocalDate.of(EXPIRY_YEAR, EXPIRY_MONTH, 1);
  private static final long AMOUNT = 10L;

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;


  @BeforeEach
  void cleanDatabase() {
    paymentsRepository.deleteAll();
  }


  @Test
  void when_payment_with_id_exist_then_correct_payment_is_returned() throws Exception {
    Payment payment = aPayment(
        ORDER_ID,
        PaymentStatus.AUTHORIZED,
        CARD_NUMBER,
        CVV,
        EXPIRY_DATE,
        CurrencyCode.USD,
        AMOUNT
    );

    Payment existingPayment = paymentsRepository.save(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + existingPayment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.order_id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.card_number_last_four").value("1115"))
        .andExpect(jsonPath("$.expiry_month").value(EXPIRY_MONTH))
        .andExpect(jsonPath("$.expiry_year").value(EXPIRY_YEAR))
        .andExpect(jsonPath("$.currency_code").value(CurrencyCode.USD.getCode()))
        .andExpect(jsonPath("$.amount").value(AMOUNT));
  }

  @Test
  void when_payment_with_id_does_not_exist_then_404_is_returned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Invalid Payment ID"));
  }


  @Test
  void when_valid_payment_is_sent_it_is_processed_and_returned_to_client() throws Exception {

    mvc.perform(MockMvcRequestBuilders.post("/payment", aPaymentRequest()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.order_id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.card_number_last_four").value("1235"))
        .andExpect(jsonPath("$.expiry_month").value(EXPIRY_MONTH))
        .andExpect(jsonPath("$.expiry_year").value(EXPIRY_YEAR))
        .andExpect(jsonPath("$.currency_code").value(CurrencyCode.USD.getCode()))
        .andExpect(jsonPath("$.amount").value(AMOUNT));
  }


  private Payment aPayment(UUID orderId, PaymentStatus paymentStatus, String cardNumber, String cvv, LocalDate expiryDate, CurrencyCode currencyCode, long amount) {
    Payment payment = new Payment();
    payment.setOrderId(orderId);
    payment.setStatus(paymentStatus);
    payment.setCardNumber(cardNumber);
    payment.setExpiryDate(expiryDate);
    payment.setCurrency(currencyCode);
    payment.setAmount(amount);
    payment.setCvv(cvv);
    return payment;
  }

  private HttpEntity<PostPaymentRequest> aPaymentRequest(){
    PostPaymentRequest paymentRequest = new PostPaymentRequest(
        ORDER_ID,
        CARD_NUMBER,
        EXPIRY_MONTH,
        EXPIRY_YEAR,
        CurrencyCode.USD,
        AMOUNT,
        CVV
    );
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PostPaymentRequest> requestBody = new HttpEntity<>(paymentRequest, headers);
    return requestBody;
  }

}
