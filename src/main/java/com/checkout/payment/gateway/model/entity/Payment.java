package com.checkout.payment.gateway.model.entity;

import com.checkout.payment.gateway.model.dto.PostPaymentRequest;
import com.checkout.payment.gateway.model.enums.CurrencyCode;
import com.checkout.payment.gateway.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @ColumnTransformer(
      write = "?::payment_status"
  )
  private PaymentStatus status;

  @Column(name = "card_number", nullable = false, length = 19)
  private String cardNumber;

  @Column(name = "expiry_date", nullable = false)
  private LocalDate expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency_code", nullable = false)
  @ColumnTransformer(
      write = "?::currency_code"       // Cast the enum to the custom 'currency_code' type when writing
  )
  private CurrencyCode currencyCode;

  @Column(name = "amount", nullable = false)
  private long amount;

  @Column(name = "cvv", nullable = false)
  private String cvv;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public CurrencyCode getCurrency() {
    return currencyCode;
  }

  public void setCurrency(CurrencyCode currencyCode) {
    this.currencyCode = currencyCode;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public void setOrderId(UUID orderId) {
    this.orderId = orderId;
  }


  public static Payment from(PostPaymentRequest paymentRequest, boolean isAuthorized){
    Payment payment = new Payment();
    payment.setOrderId(paymentRequest.orderId());
    payment.setAmount(paymentRequest.amount());
    payment.setStatus(isAuthorized ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED);
    payment.setCvv(paymentRequest.cvv());
    payment.setExpiryDate(LocalDate.of(paymentRequest.expiryYear(), paymentRequest.expiryMonth(), 1));
    payment.setCurrency(paymentRequest.currencyCode());
    payment.setCardNumber(paymentRequest.card_number());
    return payment;
  }
}
