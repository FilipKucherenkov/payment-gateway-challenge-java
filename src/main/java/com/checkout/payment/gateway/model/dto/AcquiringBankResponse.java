package com.checkout.payment.gateway.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AcquiringBankResponse(
    @JsonProperty("authorized") boolean isAuthorized,
    @JsonProperty("authorization_code") String authorization_code
){

  @Override
  public String toString() {
    return "AcquiringBankResponse{" +
        "authorized=" + isAuthorized +
        ", authorization_code=" + authorization_code +
        '}';
  }
}

