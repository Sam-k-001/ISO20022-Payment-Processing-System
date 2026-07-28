// Pacs008Generator.java
package com.fintech.payment.iso20022.generator;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.exception.PaymentProcessingException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates ISO-20022 pacs.008.001.10 FIToFICustomerCreditTransfer XML messages.
 *
 * <p>pacs.008 is the interbank settlement message exchanged between financial
 * institutions (FI-to-FI). Once the debtor's bank accepts a pain.001 request,
 * it generates a pacs.008 to route the payment through the SWIFT/SEPA network
 * to the creditor's bank for final settlement.</p>
 */
@Component
@Slf4j
public class Pacs008Generator {

    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public String generate(PaymentTransaction transaction) {
        log.debug("Generating pacs.008 message for transaction: {}", transaction.getTransactionId());

        try {
            Pacs008Message message = buildMessage(transaction);
            XmlMapper xmlMapper = XmlMapper.builder()
                .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
                .build();
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        } catch (Exception e) {
            log.error("Failed to generate pacs.008 for transaction {}: {}",
                transaction.getTransactionId(), e.getMessage());
            throw new PaymentProcessingException("ISO-20022 pacs.008 generation failed", e);
        }
    }

    private Pacs008Message buildMessage(PaymentTransaction tx) {
        return Pacs008Message.builder()
            .fiToFICustomerCreditTransfer(
                Pacs008Message.FIToFICustomerCreditTransfer.builder()
                    .groupHeader(Pacs008Message.GroupHeader.builder()
                        .messageId(tx.getPacs008MessageId())
                        .creationDateTime(LocalDateTime.now().format(ISO_DATETIME))
                        .numberOfTransactions(1)
                        .settlementMethod("CLRG")
                        .clearingSystem("TGT2") // TARGET2 RTGS
                        .build())
                    .creditTransferTransactionInformation(
                        Pacs008Message.CreditTransferTransactionInformation.builder()
                            .paymentIdentification(Pacs008Message.PaymentIdentification.builder()
                                .instructionId("INSTR-" + tx.getTransactionId())
                                .endToEndId(tx.getTransactionId())
                                .transactionId("UETR-" + tx.getTransactionId())
                                .build())
                            .interbankSettlementAmount(Pacs008Message.SettlementAmount.builder()
                                .currency(tx.getCurrency())
                                .value(tx.getAmount())
                                .build())
                            .interbankSettlementDate(LocalDateTime.now().format(ISO_DATETIME))
                            .instructingAgent(buildAgent(tx.getDebtorBic()))
                            .instructedAgent(buildAgent(tx.getCreditorBic()))
                            .debtor(Pacs008Message.Party.builder().name(tx.getDebtorName()).build())
                            .debtorAccount(Pacs008Message.Account.builder()
                                .iban(tx.getDebtorAccount())
                                .build())
                            .creditor(Pacs008Message.Party.builder().name(tx.getCreditorName()).build())
                            .creditorAccount(Pacs008Message.Account.builder()
                                .iban(tx.getCreditorAccount())
                                .build())
                            .purpose("BKDF") // Bank Defined
                            .remittanceInformation(tx.getRemittanceInfo())
                            .build())
                    .build())
            .build();
    }

    private Pacs008Message.Agent buildAgent(String bic) {
        return Pacs008Message.Agent.builder()
            .bic(bic != null ? bic : "NOTPROVIDED")
            .build();
    }

    // ======= Inner Model Classes =======

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JacksonXmlRootElement(localName = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10")
    public static class Pacs008Message {

        @JacksonXmlProperty(localName = "FIToFICstmrCdtTrf")
        private FIToFICustomerCreditTransfer fiToFICustomerCreditTransfer;

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class FIToFICustomerCreditTransfer {
            @JacksonXmlProperty(localName = "GrpHdr")
            private GroupHeader groupHeader;
            @JacksonXmlProperty(localName = "CdtTrfTxInf")
            private CreditTransferTransactionInformation creditTransferTransactionInformation;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class GroupHeader {
            @JacksonXmlProperty(localName = "MsgId") private String messageId;
            @JacksonXmlProperty(localName = "CreDtTm") private String creationDateTime;
            @JacksonXmlProperty(localName = "NbOfTxs") private int numberOfTransactions;
            @JacksonXmlProperty(localName = "SttlmInf") private String settlementMethod;
            @JacksonXmlProperty(localName = "ClrSys") private String clearingSystem;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class CreditTransferTransactionInformation {
            @JacksonXmlProperty(localName = "PmtId") private PaymentIdentification paymentIdentification;
            @JacksonXmlProperty(localName = "IntrBkSttlmAmt") private SettlementAmount interbankSettlementAmount;
            @JacksonXmlProperty(localName = "IntrBkSttlmDt") private String interbankSettlementDate;
            @JacksonXmlProperty(localName = "InstgAgt") private Agent instructingAgent;
            @JacksonXmlProperty(localName = "InstdAgt") private Agent instructedAgent;
            @JacksonXmlProperty(localName = "Dbtr") private Party debtor;
            @JacksonXmlProperty(localName = "DbtrAcct") private Account debtorAccount;
            @JacksonXmlProperty(localName = "Cdtr") private Party creditor;
            @JacksonXmlProperty(localName = "CdtrAcct") private Account creditorAccount;
            @JacksonXmlProperty(localName = "Purp") private String purpose;
            @JacksonXmlProperty(localName = "RmtInf") private String remittanceInformation;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class PaymentIdentification {
            @JacksonXmlProperty(localName = "InstrId") private String instructionId;
            @JacksonXmlProperty(localName = "EndToEndId") private String endToEndId;
            @JacksonXmlProperty(localName = "TxId") private String transactionId;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class SettlementAmount {
            @JacksonXmlProperty(isAttribute = true, localName = "Ccy") private String currency;
            @JacksonXmlProperty(localName = "") private BigDecimal value;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Agent {
            @JacksonXmlProperty(localName = "BICFI") private String bic;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Party {
            @JacksonXmlProperty(localName = "Nm") private String name;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Account {
            @JacksonXmlProperty(localName = "IBAN") private String iban;
        }
    }
}
