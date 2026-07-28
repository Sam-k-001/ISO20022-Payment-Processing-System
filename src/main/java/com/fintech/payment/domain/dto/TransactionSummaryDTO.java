// TransactionSummaryDTO.java
package com.fintech.payment.domain.dto;

import com.fintech.payment.domain.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Lightweight DTO for paginated transaction list responses */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryDTO {
    private String transactionId;
    private String debtorName;
    private String creditorName;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
