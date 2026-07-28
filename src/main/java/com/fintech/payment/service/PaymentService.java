// PaymentService.java
package com.fintech.payment.service;

import com.fintech.payment.domain.dto.PaymentRequestDTO;
import com.fintech.payment.domain.dto.PaymentResponseDTO;
import com.fintech.payment.domain.dto.TransactionSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Payment Service interface — defines the business contract
 * for all payment processing operations.
 */
public interface PaymentService {

    /**
     * Initiates a new payment transfer.
     * Generates ISO-20022 pain.001 message and persists transaction.
     *
     * @param request Validated payment initiation request
     * @return PaymentResponseDTO with transaction ID and initial status
     */
    PaymentResponseDTO initiatePayment(PaymentRequestDTO request);

    /**
     * Retrieves a specific payment transaction by its unique transaction ID.
     *
     * @param transactionId End-to-end transaction identifier
     * @return PaymentResponseDTO with full transaction details
     * @throws com.fintech.payment.exception.PaymentNotFoundException if not found
     */
    PaymentResponseDTO getPaymentByTransactionId(String transactionId);

    /**
     * Retrieves paginated transaction history.
     *
     * @param pageable Pagination parameters
     * @return Page of TransactionSummaryDTO
     */
    Page<TransactionSummaryDTO> getTransactionHistory(Pageable pageable);

    /**
     * Retrieves the ISO-20022 XML messages generated for a transaction.
     *
     * @param transactionId Transaction identifier
     * @return ISO20022MessageBundle containing XML for all message types
     */
    ISO20022MessageBundle getISO20022Messages(String transactionId);

    /**
     * Value object containing the full ISO-20022 XML message bundle
     */
    record ISO20022MessageBundle(
        String transactionId,
        String pain001Xml,
        String pacs008Xml,
        String camt053Xml
    ) {}
}
