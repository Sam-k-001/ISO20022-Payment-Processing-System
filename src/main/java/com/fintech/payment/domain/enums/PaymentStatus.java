// PaymentStatus.java
package com.fintech.payment.domain.enums;

/**
 * Represents the lifecycle states of a payment transaction,
 * aligned with ISO-20022 payment status codes.
 */
public enum PaymentStatus {

    /**
     * ACCP - AcceptedCustomerProfile
     * Payment request has been received and validated.
     */
    INITIATED,

    /**
     * ACSP - AcceptedSettlementInProcess
     * Payment is being routed through the interbank settlement network.
     */
    PROCESSING,

    /**
     * ACSC - AcceptedSettlementCompleted
     * Funds have been successfully settled to the creditor account.
     */
    SETTLED,

    /**
     * RJCT - Rejected
     * Payment was rejected due to validation failure, insufficient funds,
     * or compliance screening rejection.
     */
    FAILED
}
