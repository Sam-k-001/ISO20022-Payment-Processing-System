// PaymentResponseDTO.java
package com.fintech.payment.domain.dto;

import com.fintech.payment.domain.enums.PaymentStatus;
import com.fintech.payment.domain.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outbound DTO for payment API responses.
 * Returns sanitized transaction data — never exposes raw account numbers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long id;
    private String transactionId;
    private String debtorName;
    private String creditorName;

    /** Masked account: shows only last 4 digits — PCI-DSS compliance */
    private String debtorAccountMasked;
    private String creditorAccountMasked;

    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentType paymentType;
    private String remittanceInfo;
    private String pain001MessageId;
    private String pacs008MessageId;
    private String rejectionReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime settledAt;

    /** Convenience method: human-readable status description */
    public String getStatusDescription() {
        return switch (status) {
            case INITIATED   -> "Payment accepted and queued for processing";
            case PROCESSING  -> "Payment is being settled through interbank network";
            case SETTLED     -> "Payment successfully completed";
            case FAILED      -> "Payment rejected: " + rejectionReason;
        };
    }
}
