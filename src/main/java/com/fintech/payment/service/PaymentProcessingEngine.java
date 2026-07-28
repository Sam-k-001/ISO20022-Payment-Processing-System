// PaymentProcessingEngine.java
package com.fintech.payment.service;

import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.domain.enums.PaymentStatus;
import com.fintech.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous Payment Processing Engine.
 *
 * <p>Simulates the interbank settlement pipeline:
 * INITIATED → PROCESSING → SETTLED (or FAILED on error)</p>
 *
 * <p>In production, this would interface with:
 * - SWIFT GPI for cross-border payments
 * - TARGET2 for Eurozone RTGS
 * - CHIPS/Fedwire for USD settlements
 * - SEPA Instant Credit Transfer for EUR fast payments</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingEngine {

    private final PaymentTransactionRepository repository;
    private final SettlementService settlementService;

    @Value("${app.payment.settlement.delay-ms:500}")
    private long settlementDelayMs;

    /**
     * Processes payment asynchronously on a separate thread pool.
     * Uses @Async to prevent blocking the HTTP request thread.
     */
    @Async("paymentProcessorExecutor")
    @Transactional
    public void processPaymentAsync(Long transactionId) {
        repository.findById(transactionId).ifPresent(transaction -> {
            try {
                processPayment(transaction);
            } catch (Exception e) {
                log.error("Critical error during payment processing for transaction ID {}: {}",
                    transactionId, e.getMessage(), e);
                handleProcessingFailure(transaction, "Internal processing error: " + e.getMessage());
            }
        });
    }

    private void processPayment(PaymentTransaction transaction) throws InterruptedException {
        log.info("Processing payment: {} | Amount: {} {}",
            transaction.getTransactionId(),
            transaction.getAmount(),
            transaction.getCurrency());

        // Stage 1: Transition to PROCESSING — simulates routing through settlement network
        updateStatus(transaction, PaymentStatus.PROCESSING);
        log.info("[{}] Status: PROCESSING — routing through interbank network",
            transaction.getTransactionId());

        // Stage 2: Simulate network latency (settlement processing time)
        TimeUnit.MILLISECONDS.sleep(settlementDelayMs);

        // Stage 3: Execute settlement validation
        boolean settlementSuccess = settlementService.executeSettlement(transaction);

        if (settlementSuccess) {
            // Stage 4: Mark as SETTLED
            transaction.setStatus(PaymentStatus.SETTLED);
            transaction.setSettledAt(LocalDateTime.now());
            repository.save(transaction);
            log.info("[{}] Status: SETTLED ✓ — funds posted to creditor account {}",
                transaction.getTransactionId(),
                maskAccount(transaction.getCreditorAccount()));
        } else {
            handleProcessingFailure(transaction, "Settlement network rejection");
        }
    }

    private void updateStatus(PaymentTransaction transaction, PaymentStatus newStatus) {
        transaction.setStatus(newStatus);
        repository.save(transaction);
    }

    private void handleProcessingFailure(PaymentTransaction transaction, String reason) {
        log.warn("[{}] Status: FAILED — Reason: {}",
            transaction.getTransactionId(), reason);
        transaction.setStatus(PaymentStatus.FAILED);
        transaction.setRejectionReason(reason);
        repository.save(transaction);
    }

    private String maskAccount(String account) {
        if (account == null || account.length() < 4) return "****";
        return "*".repeat(account.length() - 4) + account.substring(account.length() - 4);
    }
}
