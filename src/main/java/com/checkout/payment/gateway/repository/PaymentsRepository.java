package com.checkout.payment.gateway.repository;


import com.checkout.payment.gateway.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, UUID> {

}
