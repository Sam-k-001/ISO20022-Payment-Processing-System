// PaymentType.java
package com.fintech.payment.domain.enums;

/**
 * Classifies payment type per ISO-20022 service level codes.
 */
public enum PaymentType {
    /** Standard credit transfer (SEPA/ACH equivalent) */
    CREDIT_TRANSFER,
    /** Real-Time Gross Settlement — high-value immediate payment */
    RTGS,
    /** SWIFT cross-border financial institution transfer */
    SWIFT,
    /** Domestic low-value batch payment */
    ACH
}
