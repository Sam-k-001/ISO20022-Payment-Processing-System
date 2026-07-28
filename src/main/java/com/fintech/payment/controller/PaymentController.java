// PaymentController.java
package com.fintech.payment.controller;

import com.fintech.payment.domain.dto.PaymentRequestDTO;
import com.fintech.payment.domain.dto.PaymentResponseDTO;
import com.fintech.payment.domain.dto.TransactionSummaryDTO;
import com.fintech.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for ISO-20022 Payment Processing.
 *
 * <p>Exposes RESTful endpoints for payment initiation, status inquiry,
 * and transaction history — aligned with PSD2 Open Banking standards.</p>
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Processing", description = "ISO-20022 compliant payment processing API")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/v1/payments/transfer
     * Initiates a new payment transfer (ISO-20022 pain.001)
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Initiate Payment Transfer",
        description = "Initiates a new customer credit transfer. Generates ISO-20022 pain.001 " +
                      "message and queues payment for interbank settlement."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment request data"),
        @ApiResponse(responseCode = "422", description = "Payment processing error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentResponseDTO> initiateTransfer(
        @Valid @RequestBody PaymentRequestDTO request
    ) {
        log.info("API: POST /transfer | Debtor: {} | Amount: {} {}",
            request.getDebtorName(), request.getAmount(), request.getCurrency());

        PaymentResponseDTO response = paymentService.initiatePayment(request);

        log.info("API: Payment initiated successfully | TransactionId: {}", response.getTransactionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/payments/{transactionId}
     * Retrieves payment status by transaction ID
     */
    @GetMapping("/{transactionId}")
    @Operation(
        summary = "Get Payment Status",
        description = "Retrieves payment transaction details and current processing status."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(
        @Parameter(description = "End-to-end transaction identifier", example = "TXN-ABC123DEF456GHI7")
        @PathVariable String transactionId
    ) {
        log.debug("API: GET /payments/{}", transactionId);
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    /**
     * GET /api/v1/payments
     * Retrieves paginated transaction history
     */
    @GetMapping
    @Operation(
        summary = "Get Transaction History",
        description = "Returns paginated payment transaction history ordered by creation date."
    )
    public ResponseEntity<Page<TransactionSummaryDTO>> getTransactionHistory(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Sort order") @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Pageable pageable = PageRequest.of(
            page,
            Math.min(size, 100), // cap at 100 per page
            Sort.by(Sort.Direction.DESC, sort)
        );
        log.debug("API: GET /payments?page={}&size={}", page, size);
        return ResponseEntity.ok(paymentService.getTransactionHistory(pageable));
    }

    /**
     * GET /api/v1/payments/{transactionId}/messages
     * Returns generated ISO-20022 XML messages for a transaction
     */
    @GetMapping("/{transactionId}/messages")
    @Operation(
        summary = "Get ISO-20022 XML Messages",
        description = "Returns the generated ISO-20022 XML message chain (pain.001, pacs.008, camt.053)."
    )
    public ResponseEntity<Map<String, String>> getISO20022Messages(
        @PathVariable String transactionId
    ) {
        log.debug("API: GET /payments/{}/messages", transactionId);

        PaymentService.ISO20022MessageBundle bundle =
            paymentService.getISO20022Messages(transactionId);

        Map<String, String> response = Map.of(
            "transactionId", bundle.transactionId(),
            "pain001_xml", bundle.pain001Xml(),
            "pacs008_xml", bundle.pacs008Xml(),
            "camt053_xml", bundle.camt053Xml()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/payments/health
     * Service health check endpoint (supplementary to actuator)
     */
    @GetMapping("/health")
    @Operation(summary = "Payment Service Health Check")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "ISO-20022 Payment Processing System",
            "status", "OPERATIONAL",
            "version", "1.0.0-RELEASE",
            "standard", "ISO 20022"
        ));
    }
}
