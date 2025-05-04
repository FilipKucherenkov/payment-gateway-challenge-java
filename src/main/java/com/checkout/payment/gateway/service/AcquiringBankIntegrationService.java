package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.AcquiringBankException;
import com.checkout.payment.gateway.model.dto.AcquiringBankRequest;
import com.checkout.payment.gateway.model.dto.AcquiringBankResponse;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class AcquiringBankIntegrationService {

  @Value("${acquiring-bank.url}")
  private String acquiringBankUrl;

  private final RestTemplate restTemplate;


  public AcquiringBankIntegrationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  public boolean initiatePayment(PostPaymentRequest paymentRequest) {
    AcquiringBankRequest acquiringBankRequest = AcquiringBankRequest.from(paymentRequest);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AcquiringBankRequest> requestBody = new HttpEntity<>(acquiringBankRequest, headers);

    try {
      ResponseEntity<AcquiringBankResponse> response = restTemplate.postForEntity(
          acquiringBankUrl,
          requestBody,
          AcquiringBankResponse.class
      );

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody().isAuthorized();
      }

      throw new AcquiringBankException("Unexpected response from acquiring bank: " + response.getStatusCode());

    } catch (HttpStatusCodeException ex) {
      HttpStatusCode status = ex.getStatusCode();

      if (status == HttpStatus.SERVICE_UNAVAILABLE) {
        throw new AcquiringBankException("Acquiring bank is currently down");
      } else if (status == HttpStatus.BAD_REQUEST) {
        throw new AcquiringBankException("Acquirer could not process the request");
      } else {
        throw new AcquiringBankException("Unexpected response from acquiring bank: " + status);
      }

    } catch (Exception ex) {

      throw new AcquiringBankException("Failed to initiate payment with acquiring bank");
    }
  }
}
