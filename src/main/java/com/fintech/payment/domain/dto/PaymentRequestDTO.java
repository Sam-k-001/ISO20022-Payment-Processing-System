// PaymentRequestDTO.java
package com.fintech.payment.domain.dto;

import com.fintech.payment.domain.enums.PaymentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Inbound DTO for payment initiation requests.
 * Maps to ISO-20022 pain.001 CustomerCreditTransferInitiation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotBlank(message = "Debtor name is required")
    @Size(max = 140, message = "Debtor name must not exceed 140 characters")
    private String debtorName;

    @NotBlank(message = "Creditor name is required")
    @Size(max = 140, message = "Creditor name must not exceed 140 characters")
    private String creditorName;

    @NotBlank(message = "Debtor account (IBAN) is required")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$",
        message = "Debtor account must be a valid IBAN format"
    )
    private String debtorAccount;

    @NotBlank(message = "Creditor account (IBAN) is required")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$",
        message = "Creditor account must be a valid IBAN format"
    )
    private String creditorAccount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount exceeds maximum transfer limit")
    @Digits(integer = 13, fraction = 2, message = "Amount format invalid")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(
        regexp = "^(USD|EUR|GBP|JPY|CHF|AUD|CAD)$",
        message = "Currency must be a supported ISO-4217 code"
    )
    private String currency;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @Size(max = 140, message = "Remittance info must not exceed 140 characters")
    private String remittanceInfo;

    @Pattern(
        regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$",
        message = "Debtor BIC must be a valid SWIFT BIC format"
    )
    private String debtorBic;

    @Pattern(
        regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$",
        message = "Creditor BIC must be a valid SWIFT BIC format"
    )
    private String creditorBic;
}
