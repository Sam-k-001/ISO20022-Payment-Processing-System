// PaymentTransaction.java
package com.fintech.payment.domain.entity;

import com.fintech.payment.domain.enums.PaymentStatus;
import com.fintech.payment.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Core JPA entity representing a payment transaction record.
 *
 * <p>Persists all relevant payment data aligned with ISO-20022
 * CreditTransferTransaction (pain.001.001.09) structure.</p>
 */
@Entity
@Table(
    name = "payment_transactions",
    indexes = {
        @Index(name = "idx_transaction_id", columnList = "transactionId", unique = true),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_debtor_account", columnList = "debtorAccount"),
        @Index(name = "idx_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"debtorAccount", "creditorAccount"}) // PCI-DSS: never log account numbers
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Unique end-to-end transaction identifier (E2EId in ISO-20022).
     * Format: TXN-{UUID} — immutable once assigned.
     */
    @Column(name = "transaction_id", nullable = false, unique = true, length = 50)
    private String transactionId;

    /** Ordering party name (Debtor - the party sending funds) */
    @Column(name = "debtor_name", nullable = false, length = 140)
    private String debtorName;

    /** Beneficiary party name (Creditor - the party receiving funds) */
    @Column(name = "creditor_name", nullable = false, length = 140)
    private String creditorName;

    /**
     * Debtor IBAN/account number.
     * In production: this field should be encrypted at rest (AES-256).
     */
    @Column(name = "debtor_account", nullable = false, length = 34)
    private String debtorAccount;

    /**
     * Creditor IBAN/account number.
     * In production: this field should be encrypted at rest (AES-256).
     */
    @Column(name = "creditor_account", nullable = false, length = 34)
    private String creditorAccount;

    /** Payment amount — using BigDecimal for financial precision (never Float/Double) */
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    /** ISO-4217 currency code (e.g., USD, EUR, GBP) */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /** Current lifecycle status of the payment */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    /** Classification of payment type for routing purposes */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    /** Reference message for the payment (e.g., invoice number) */
    @Column(name = "remittance_info", length = 140)
    private String remittanceInfo;

    /** BIC/SWIFT code of the debtor's financial institution */
    @Column(name = "debtor_bic", length = 11)
    private String debtorBic;

    /** BIC/SWIFT code of the creditor's financial institution */
    @Column(name = "creditor_bic", length = 11)
    private String creditorBic;

    /** Generated ISO-20022 pain.001 XML message reference */
    @Column(name = "pain001_message_id", length = 35)
    private String pain001MessageId;

    /** Generated ISO-20022 pacs.008 XML message reference */
    @Column(name = "pacs008_message_id", length = 35)
    private String pacs008MessageId;

    /** ISO-20022 rejection reason code if status is FAILED */
    @Column(name = "rejection_reason", length = 350)
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Timestamp when settlement was completed */
    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    /** Version field for optimistic locking — prevents concurrent modification */
    @Version
    @Column(name = "version")
    private Long version;
}
