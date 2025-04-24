package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.dto.ErrorResponse;
import com.checkout.payment.gateway.model.dto.ValidationErrorResponse;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(PageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(PageNotFoundException ex) {
    LOGGER.error("Payment with such id does not exist", ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(PaymentConflictException.class)
  public ResponseEntity<ErrorResponse> handleException(PaymentConflictException ex) {
    LOGGER.error("Duplicate payments are not allowed", ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(AcquiringBankException.class)
  public ResponseEntity<ErrorResponse> handleException(AcquiringBankException ex) {
    LOGGER.error("Acquiring Bank has encountered a problem processing the request", ex);
    return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_GATEWAY);
  }


  @ExceptionHandler(InvalidPaymentRequestException.class)
  public ResponseEntity<ValidationErrorResponse> handleException(InvalidPaymentRequestException ex) {
    LOGGER.error("Payment request was rejected because it did not pass validation", ex);
    return new ResponseEntity<>(new ValidationErrorResponse(PaymentStatus.REJECTED, ex.getErrors()), HttpStatus.BAD_REQUEST);
  }

}
