// Camt053Generator.java
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
 * Generates ISO-20022 camt.053.001.08 BankToCustomerStatement XML messages.
 *
 * <p>camt.053 is the account statement / transaction status report sent by the
 * creditor's financial institution to confirm that a credit has been posted
 * to the beneficiary's account. This closes the payment lifecycle loop.</p>
 */
@Component
@Slf4j
public class Camt053Generator {

    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public String generate(PaymentTransaction transaction) {
        log.debug("Generating camt.053 message for transaction: {}", transaction.getTransactionId());

        try {
            Camt053Message message = buildMessage(transaction);
            XmlMapper xmlMapper = XmlMapper.builder()
                .configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
                .build();
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        } catch (Exception e) {
            log.error("Failed to generate camt.053 for transaction {}: {}",
                transaction.getTransactionId(), e.getMessage());
            throw new PaymentProcessingException("ISO-20022 camt.053 generation failed", e);
        }
    }

    private Camt053Message buildMessage(PaymentTransaction tx) {
        String creditDebitIndicator = "CRDT"; // Credit entry on creditor's statement

        return Camt053Message.builder()
            .bankToCustomerStatement(Camt053Message.BankToCustomerStatement.builder()
                .groupHeader(Camt053Message.GroupHeader.builder()
                    .messageId("CAMT053-" + tx.getTransactionId())
                    .creationDateTime(LocalDateTime.now().format(ISO_DATETIME))
                    .messagePagination(Camt053Message.Pagination.builder()
                        .pageNumber("1")
                        .lastPageIndicator(true)
                        .build())
                    .build())
                .statement(Camt053Message.Statement.builder()
                    .statementId("STMT-" + tx.getTransactionId())
                    .electronicSequenceNumber("1")
                    .creationDateTime(LocalDateTime.now().format(ISO_DATETIME))
                    .fromDateTime(tx.getCreatedAt().format(ISO_DATETIME))
                    .toDateTime(LocalDateTime.now().format(ISO_DATETIME))
                    .account(Camt053Message.StatementAccount.builder()
                        .iban(tx.getCreditorAccount())
                        .currency(tx.getCurrency())
                        .build())
                    .entry(Camt053Message.Entry.builder()
                        .amount(tx.getAmount())
                        .currency(tx.getCurrency())
                        .creditDebitIndicator(creditDebitIndicator)
                        .status("BOOK") // Booked — confirmed posting
                        .bookingDateTime(LocalDateTime.now().format(ISO_DATETIME))
                        .valueDate(LocalDateTime.now().format(ISO_DATETIME))
                        .entryDetails(Camt053Message.EntryDetails.builder()
                            .transactionDetails(Camt053Message.TransactionDetails.builder()
                                .endToEndId(tx.getTransactionId())
                                .instructedAmount(tx.getAmount())
                                .instructedCurrency(tx.getCurrency())
                                .debtorName(tx.getDebtorName())
                                .remittanceInfo(tx.getRemittanceInfo() != null
                                    ? tx.getRemittanceInfo()
                                    : "Settled: " + tx.getTransactionId())
                                .build())
                            .build())
                        .build())
                    .build())
                .build())
            .build();
    }

    // ======= Inner Model Classes =======

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @JacksonXmlRootElement(localName = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:camt.053.001.08")
    public static class Camt053Message {

        @JacksonXmlProperty(localName = "BkToCstmrStmt")
        private BankToCustomerStatement bankToCustomerStatement;

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class BankToCustomerStatement {
            @JacksonXmlProperty(localName = "GrpHdr") private GroupHeader groupHeader;
            @JacksonXmlProperty(localName = "Stmt") private Statement statement;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class GroupHeader {
            @JacksonXmlProperty(localName = "MsgId") private String messageId;
            @JacksonXmlProperty(localName = "CreDtTm") private String creationDateTime;
            @JacksonXmlProperty(localName = "MsgPgntn") private Pagination messagePagination;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Pagination {
            @JacksonXmlProperty(localName = "PgNb") private String pageNumber;
            @JacksonXmlProperty(localName = "LastPgInd") private boolean lastPageIndicator;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Statement {
            @JacksonXmlProperty(localName = "Id") private String statementId;
            @JacksonXmlProperty(localName = "ElctrncSeqNb") private String electronicSequenceNumber;
            @JacksonXmlProperty(localName = "CreDtTm") private String creationDateTime;
            @JacksonXmlProperty(localName = "FrToDt") private String fromDateTime;
            @JacksonXmlProperty(localName = "ToDt") private String toDateTime;
            @JacksonXmlProperty(localName = "Acct") private StatementAccount account;
            @JacksonXmlProperty(localName = "Ntry") private Entry entry;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class StatementAccount {
            @JacksonXmlProperty(localName = "IBAN") private String iban;
            @JacksonXmlProperty(localName = "Ccy") private String currency;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class Entry {
            @JacksonXmlProperty(localName = "Amt") private BigDecimal amount;
            @JacksonXmlProperty(isAttribute = true, localName = "Ccy") private String currency;
            @JacksonXmlProperty(localName = "CdtDbtInd") private String creditDebitIndicator;
            @JacksonXmlProperty(localName = "Sts") private String status;
            @JacksonXmlProperty(localName = "BookgDt") private String bookingDateTime;
            @JacksonXmlProperty(localName = "ValDt") private String valueDate;
            @JacksonXmlProperty(localName = "NtryDtls") private EntryDetails entryDetails;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class EntryDetails {
            @JacksonXmlProperty(localName = "TxDtls") private TransactionDetails transactionDetails;
        }

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class TransactionDetails {
            @JacksonXmlProperty(localName = "EndToEndId") private String endToEndId;
            @JacksonXmlProperty(localName = "InstdAmt") private BigDecimal instructedAmount;
            @JacksonXmlProperty(localName = "InstdCcy") private String instructedCurrency;
            @JacksonXmlProperty(localName = "DbtrNm") private String debtorName;
            @JacksonXmlProperty(localName = "RmtInf") private String remittanceInfo;
        }
    }
}
