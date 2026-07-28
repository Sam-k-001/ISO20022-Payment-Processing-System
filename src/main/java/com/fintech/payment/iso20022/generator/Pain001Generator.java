// Pain001Generator.java
package com.fintech.payment.iso20022.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.exception.PaymentProcessingException;
import com.fintech.payment.iso20022.model.Pain001Message;
import com.fintech.payment.iso20022.model.Pain001Message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates ISO-20022 pain.001.001.09 CustomerCreditTransferInitiation XML messages.
 *
 * <p>pain.001 is the payment initiation message sent by the ordering customer
 * (debtor) to their bank requesting a credit transfer. This is the entry point
 * of the ISO-20022 payment workflow.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Pain001Generator {

    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String generate(PaymentTransaction transaction) {
        log.debug("Generating pain.001 message for transaction: {}", transaction.getTransactionId());

        try {
            Pain001Message message = buildMessage(transaction);
            return serializeToXml(message);
        } catch (Exception e) {
            log.error("Failed to generate pain.001 for transaction {}: {}",
                transaction.getTransactionId(), e.getMessage());
            throw new PaymentProcessingException("ISO-20022 pain.001 generation failed", e);
        }
    }

    private Pain001Message buildMessage(PaymentTransaction tx) {
        return Pain001Message.builder()
            .customerCreditTransferInitiation(
                CustomerCreditTransferInitiation.builder()
                    .groupHeader(buildGroupHeader(tx))
                    .paymentInformation(buildPaymentInformation(tx))
                    .build()
            )
            .build();
    }

    private GroupHeader buildGroupHeader(PaymentTransaction tx) {
        return GroupHeader.builder()
            .messageId(tx.getPain001MessageId())
            .creationDateTime(LocalDateTime.now().format(ISO_DATETIME))
            .numberOfTransactions(1)
            .controlSum(tx.getAmount())
            .initiatingParty(InitiatingParty.builder().name(tx.getDebtorName()).build())
            .build();
    }

    private PaymentInformation buildPaymentInformation(PaymentTransaction tx) {
        return PaymentInformation.builder()
            .paymentInformationId("PMTINF-" + tx.getTransactionId())
            .paymentMethod("TRF")
            .requestedExecutionDate(LocalDate.now().format(ISO_DATE))
            .debtor(Party.builder().name(tx.getDebtorName()).build())
            .debtorAccount(buildAccount(tx.getDebtorAccount()))
            .debtorAgent(buildFinancialInstitution(tx.getDebtorBic()))
            .creditTransferTransactionInfo(buildCreditTransferInfo(tx))
            .build();
    }

    private CreditTransferTransactionInfo buildCreditTransferInfo(PaymentTransaction tx) {
        return CreditTransferTransactionInfo.builder()
            .paymentId(PaymentIdentification.builder()
                .endToEndId(tx.getTransactionId())
                .build())
            .amount(Amount.builder()
                .instructedAmount(InstructedAmount.builder()
                    .currency(tx.getCurrency())
                    .value(tx.getAmount())
                    .build())
                .build())
            .creditorAgent(buildFinancialInstitution(tx.getCreditorBic()))
            .creditor(Party.builder().name(tx.getCreditorName()).build())
            .creditorAccount(buildAccount(tx.getCreditorAccount()))
            .remittanceInfo(RemittanceInfo.builder()
                .unstructured(tx.getRemittanceInfo() != null
                    ? tx.getRemittanceInfo()
                    : "Payment: " + tx.getTransactionId())
                .build())
            .build();
    }

    private Account buildAccount(String iban) {
        return Account.builder()
            .id(AccountIdentification.builder().iban(iban).build())
            .build();
    }

    private FinancialInstitution buildFinancialInstitution(String bic) {
        return FinancialInstitution.builder()
            .financialInstitutionId(FinancialInstitutionId.builder()
                .bic(bic != null ? bic : "NOTPROVIDED")
                .build())
            .build();
    }

    private String serializeToXml(Pain001Message message) throws JsonProcessingException {
        XmlMapper xmlMapper = XmlMapper.builder()
            .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
            .build();
        return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }
}
