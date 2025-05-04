package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.AcquiringBankException;
import com.checkout.payment.gateway.model.dto.AcquiringBankRequest;
import com.checkout.payment.gateway.model.dto.AcquiringBankResponse;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcquiringBankIntegrationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private HttpStatusCodeException exception;

  @Captor
  private ArgumentCaptor<HttpEntity<AcquiringBankRequest>> httpEntityCaptor;

  @InjectMocks
  private AcquiringBankIntegrationService underTest;


  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(underTest, "acquiringBankUrl", "http://fake-bank.com");
  }

  @Test
  void initiate_payment_should_return_true_when_authorized() {
    PostPaymentRequest request = validRequest();
    AcquiringBankResponse responseBody = new AcquiringBankResponse(true, "code");
    ResponseEntity<AcquiringBankResponse> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class))).thenReturn(responseEntity);

    boolean result = underTest.initiatePayment(request);

    assertThat(result).isTrue();
  }

  @Test
  void initiate_payment_should_return_false_when_not_authorized() {
    PostPaymentRequest request = validRequest();
    AcquiringBankResponse responseBody = new AcquiringBankResponse(false, "code");
    ResponseEntity<AcquiringBankResponse> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class))).thenReturn(responseEntity);

    boolean result = underTest.initiatePayment(request);

    assertThat(result).isFalse();
  }

  @Test
  void initiate_payment_should_throw_exception_on_unexpected_status() {
    PostPaymentRequest request = validRequest();

    when(exception.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class))).thenThrow(exception);

    AcquiringBankException ex = assertThrows(
        AcquiringBankException.class,
        () -> underTest.initiatePayment(request)
    );

    assertThat(ex.getMessage()).contains("Unexpected response from acquiring bank");
  }

  @Test
  void initiate_payment_should_throw_on_service_unavailable() {
    PostPaymentRequest request = validRequest();

    when(exception.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);
    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class))).thenThrow(exception);

    AcquiringBankException ex = assertThrows(
        AcquiringBankException.class,
        () -> underTest.initiatePayment(request)
    );

    assertThat(ex.getMessage()).isEqualTo("Acquiring bank is currently down");
  }

  @Test
  void initiate_payment_should_throw_on_bad_request() {
    PostPaymentRequest request = validRequest();

    when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class))).thenThrow(exception);

    AcquiringBankException ex = assertThrows(
        AcquiringBankException.class,
        () -> underTest.initiatePayment(request)
    );

    assertThat(ex.getMessage()).isEqualTo("Acquirer could not process the request");
  }

  @Test
  void initiate_payment_should_throw_generic_exception_on_runtime_failure() {
    PostPaymentRequest request = validRequest();

    when(restTemplate.postForEntity(anyString(), any(), eq(AcquiringBankResponse.class)))
        .thenThrow(new RuntimeException("connection timeout"));

    AcquiringBankException ex = assertThrows(
        AcquiringBankException.class,
        () -> underTest.initiatePayment(request)
    );

    assertThat(ex.getMessage()).isEqualTo("Failed to initiate payment with acquiring bank");
  }

  @Test
  void initiate_payment_should_send_json_content_type_header() {
    PostPaymentRequest request = validRequest();
    AcquiringBankResponse responseBody = new AcquiringBankResponse(true, "code");
    ResponseEntity<AcquiringBankResponse> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

    when(restTemplate.postForEntity(anyString(), httpEntityCaptor.capture(), eq(AcquiringBankResponse.class)))
        .thenReturn(responseEntity);

    underTest.initiatePayment(request);

    assertThat(httpEntityCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  private PostPaymentRequest validRequest() {
    return new PostPaymentRequest(
        UUID.randomUUID(),
        "4111111111111111",
        12,
        YearMonth.now().plusYears(1).getYear(),
        CurrencyCode.USD,
        100L,
        "123"
    );
  }
}