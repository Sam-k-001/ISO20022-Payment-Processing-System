// PaymentServiceTest.java
package com.fintech.payment.service;

import com.fintech.payment.domain.dto.PaymentRequestDTO;
import com.fintech.payment.domain.dto.PaymentResponseDTO;
import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.domain.enums.PaymentStatus;
import com.fintech.payment.domain.enums.PaymentType;
import com.fintech.payment.exception.PaymentNotFoundException;
import com.fintech.payment.iso20022.factory.ISO20022MessageFactory;
import com.fintech.payment.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock private PaymentTransactionRepository repository;
    @Mock private ISO20022MessageFactory messageFactory;
    @Mock private PaymentProcessingEngine processingEngine;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequestDTO validRequest;
    private PaymentTransaction savedTransaction;

    @BeforeEach
    void setUp() {
        validRequest = PaymentRequestDTO.builder()
            .debtorName("John Doe")
            .creditorName("Jane Smith")
            .debtorAccount("GB29NWBK60161331926819")
            .creditorAccount("DE89370400440532013000")
            .amount(new BigDecimal("1500.00"))
            .currency("EUR")
            .paymentType(PaymentType.CREDIT_TRANSFER)
            .remittanceInfo("Invoice INV-2024-001")
            .debtorBic("NWBKGB2L")
            .creditorBic("DEUTDEDB")
            .build();

        savedTransaction = PaymentTransaction.builder()
            .id(1L)
            .transactionId("TXN-ABC123DEF456GHI7")
            .debtorName("John Doe")
            .creditorName("Jane Smith")
            .debtorAccount("GB29NWBK60161331926819")
            .creditorAccount("DE89370400440532013000")
            .amount(new BigDecimal("1500.00"))
            .currency("EUR")
            .status(PaymentStatus.INITIATED)
            .paymentType(PaymentType.CREDIT_TRANSFER)
            .pain001MessageId("PAIN001-TXN-ABC123DEF456GHI7")
            .pacs008MessageId("PACS008-TXN-ABC123DEF456GHI7")
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should successfully initiate a payment and return INITIATED status")
    void shouldInitiatePaymentSuccessfully() {
        when(repository.save(any(PaymentTransaction.class))).thenReturn(savedTransaction);
        when(messageFactory.generateMessageChain(any())).thenReturn(
            new ISO20022MessageFactory.MessageChain("<pain001/>", "<pacs008/>", "<camt053/>")
        );
        doNothing().when(processingEngine).processPaymentAsync(anyLong());

        PaymentResponseDTO response = paymentService.initiatePayment(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(response.getTransactionId()).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(response.getCurrency()).isEqualTo("EUR");
        assertThat(response.getDebtorAccountMasked()).doesNotContain("GB29NWBK60161331926819");

        verify(repository, times(1)).save(any(PaymentTransaction.class));
        verify(messageFactory, times(1)).generateMessageChain(any());
        verify(processingEngine, times(1)).processPaymentAsync(anyLong());
    }

    @Test
    @DisplayName("Should mask account numbers in response — PCI-DSS compliance")
    void shouldMaskAccountNumbersInResponse() {
        when(repository.save(any(PaymentTransaction.class))).thenReturn(savedTransaction);
        when(messageFactory.generateMessageChain(any())).thenReturn(
            new ISO20022MessageFactory.MessageChain("<pain001/>", "<pacs008/>", "<camt053/>")
        );

        PaymentResponseDTO response = paymentService.initiatePayment(validRequest);

        assertThat(response.getDebtorAccountMasked()).endsWith("6819");
        assertThat(response.getDebtorAccountMasked()).startsWith("*");
        assertThat(response.getCreditorAccountMasked()).endsWith("3000");
    }

    @Test
    @DisplayName("Should throw PaymentNotFoundException for non-existent transaction")
    void shouldThrowNotFoundForMissingTransaction() {
        when(repository.findByTransactionId("INVALID-ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByTransactionId("INVALID-ID"))
            .isInstanceOf(PaymentNotFoundException.class)
            .hasMessageContaining("INVALID-ID");
    }

    @Test
    @DisplayName("Should retrieve payment by transaction ID")
    void shouldGetPaymentByTransactionId() {
        when(repository.findByTransactionId("TXN-ABC123DEF456GHI7"))
            .thenReturn(Optional.of(savedTransaction));

        PaymentResponseDTO response = paymentService.getPaymentByTransactionId("TXN-ABC123DEF456GHI7");

        assertThat(response.getTransactionId()).isEqualTo("TXN-ABC123DEF456GHI7");
        assertThat(response.getDebtorName()).isEqualTo("John Doe");
    }
}
