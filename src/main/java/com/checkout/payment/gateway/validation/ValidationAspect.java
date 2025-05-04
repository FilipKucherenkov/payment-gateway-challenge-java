package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.exception.InvalidPaymentRequestException;
import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
public class ValidationAspect {


  @Before("@annotation(com.checkout.payment.gateway.validation.ValidatePaymentRequest) && args(request,..)")
  public void validatePaymentRequest(PostPaymentRequest request) throws InvalidPaymentRequestException {
    List<String> errors = new ArrayList<>();

    if (Objects.isNull(request.card_number()) || !request.card_number().matches("\\d{14,19}")) {
      errors.add("Card number must be 14-19 digits long and numeric");
    }

    if (request.expiryMonth() < 1 || request.expiryMonth() > 12) {
      errors.add("Expiry month must be between 1 and 12");
    }else{
      YearMonth now = YearMonth.now();
      YearMonth expiry = YearMonth.of(request.expiryYear(), request.expiryMonth());
      if (expiry.isBefore(now)) {
        errors.add("Card expiry date must be in the future");
      }
    }

    if (Objects.isNull(request.orderId())) {
      errors.add("OrderId is required");
    }

    if (Objects.isNull(request.currencyCode())) {
      errors.add("Currency is required");
    }

    if (request.amount() <= 0) {
      errors.add("Amount must be greater than 0");
    }

    if (Objects.isNull(request.cvv()) || !request.cvv().matches("\\d{3,4}")) {
      errors.add("CVV must be 3 or 4 digit numeric");
    }

    if (!errors.isEmpty()) {
      throw new InvalidPaymentRequestException(errors);
    }
  }
}
