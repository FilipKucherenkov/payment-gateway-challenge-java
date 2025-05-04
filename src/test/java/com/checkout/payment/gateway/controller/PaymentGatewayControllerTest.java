package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.entity.Payment;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  private static final UUID ORDER_ID = UUID.fromString("2e4c9cf2-6936-469e-a438-4a4c49a13a68");
  private static final String AUTHORIZED_CARD_NUMBER = "4111111111111115";
  private static final String DECLINED_CARD_NUMBER = "4111111111111114";

  private static final String CVV = "123";
  private static final int EXPIRY_MONTH = 12;
  private static final int EXPIRY_YEAR = 2150;
  private static final LocalDate EXPIRY_DATE = LocalDate.of(EXPIRY_YEAR, EXPIRY_MONTH, 1);
  private static final long AMOUNT = 10L;


  private static final String INVALID_CARD_NUMBER = "4";
  private static final String INVALID_CVV = "1";
  private static final int INVALID_EXPIRY_YEAR = LocalDate.now().getYear() - 1;
  private static final long INVALID_AMOUNT = 0L;


  @Autowired
  private MockMvc mvc;
  @Autowired
  private PaymentsRepository paymentsRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void cleanDatabase() {
    paymentsRepository.deleteAll();
  }


  @Test
  void when_payment_with_id_exist_then_correct_payment_is_returned() throws Exception {
    Payment payment = aPayment(
        ORDER_ID,
        PaymentStatus.AUTHORIZED,
        AUTHORIZED_CARD_NUMBER,
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
  void when_valid_payment_is_sent_and_processed_successfully_by_acquirer_correct_response_is_returned_to_client() throws Exception {
    PostPaymentRequest paymentRequest = new PostPaymentRequest(
        ORDER_ID,
        AUTHORIZED_CARD_NUMBER,
        EXPIRY_MONTH,
        EXPIRY_YEAR,
        CurrencyCode.USD,
        AMOUNT,
        CVV
    );
    String jsonBody = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
        .andExpect(status().is2xxSuccessful())
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
  void when_valid_payment_is_sent_and_declined_by_acquirer_correct_response_is_returned_to_client() throws Exception {
    PostPaymentRequest paymentRequest = new PostPaymentRequest(
        ORDER_ID,
        DECLINED_CARD_NUMBER,
        EXPIRY_MONTH,
        EXPIRY_YEAR,
        CurrencyCode.USD,
        AMOUNT,
        CVV
    );
    String jsonBody = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.order_id").value(ORDER_ID.toString()))
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
        .andExpect(jsonPath("$.card_number_last_four").value("1114"))
        .andExpect(jsonPath("$.expiry_month").value(EXPIRY_MONTH))
        .andExpect(jsonPath("$.expiry_year").value(EXPIRY_YEAR))
        .andExpect(jsonPath("$.currency_code").value(CurrencyCode.USD.getCode()))
        .andExpect(jsonPath("$.amount").value(AMOUNT));
  }


  @Test
  void when_invalid_payment_is_sent_correct_response_is_returned_to_client() throws Exception {
    PostPaymentRequest paymentRequest = new PostPaymentRequest(
        ORDER_ID,
        INVALID_CARD_NUMBER,
        EXPIRY_MONTH,
        INVALID_EXPIRY_YEAR,
        CurrencyCode.USD,
        INVALID_AMOUNT,
        INVALID_CVV
    );
    String jsonBody = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()))
        .andExpect(jsonPath("$.errors[0]").value("Card number must be 14-19 digits long and numeric"))
        .andExpect(jsonPath("$.errors[1]").value("Card expiry date must be in the future"))
        .andExpect(jsonPath("$.errors[2]").value("Amount must be greater than 0"))
        .andExpect(jsonPath("$.errors[3]").value("CVV must be 3 or 4 digit numeric"));
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

}
