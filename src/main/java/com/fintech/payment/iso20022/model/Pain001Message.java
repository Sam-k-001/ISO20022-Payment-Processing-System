// Pain001Message.java
package com.fintech.payment.iso20022.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ISO-20022 pain.001.001.09 — CustomerCreditTransferInitiation
 *
 * <p>Represents the XML message structure for a customer payment
 * initiation request. This is the first message in the payment chain,
 * sent from the ordering customer to their financial institution.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.09")
public class Pain001Message {

    @JacksonXmlProperty(localName = "CstmrCdtTrfInitn")
    private CustomerCreditTransferInitiation customerCreditTransferInitiation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCreditTransferInitiation {
        @JacksonXmlProperty(localName = "GrpHdr")
        private GroupHeader groupHeader;

        @JacksonXmlProperty(localName = "PmtInf")
        private PaymentInformation paymentInformation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupHeader {
        @JacksonXmlProperty(localName = "MsgId")
        private String messageId;

        @JacksonXmlProperty(localName = "CreDtTm")
        private String creationDateTime;

        @JacksonXmlProperty(localName = "NbOfTxs")
        private int numberOfTransactions;

        @JacksonXmlProperty(localName = "CtrlSum")
        private BigDecimal controlSum;

        @JacksonXmlProperty(localName = "InitgPty")
        private InitiatingParty initiatingParty;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitiatingParty {
        @JacksonXmlProperty(localName = "Nm")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInformation {
        @JacksonXmlProperty(localName = "PmtInfId")
        private String paymentInformationId;

        @JacksonXmlProperty(localName = "PmtMtd")
        private String paymentMethod;

        @JacksonXmlProperty(localName = "ReqdExctnDt")
        private String requestedExecutionDate;

        @JacksonXmlProperty(localName = "Dbtr")
        private Party debtor;

        @JacksonXmlProperty(localName = "DbtrAcct")
        private Account debtorAccount;

        @JacksonXmlProperty(localName = "DbtrAgt")
        private FinancialInstitution debtorAgent;

        @JacksonXmlProperty(localName = "CdtTrfTxInf")
        private CreditTransferTransactionInfo creditTransferTransactionInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Party {
        @JacksonXmlProperty(localName = "Nm")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account {
        @JacksonXmlProperty(localName = "Id")
        private AccountIdentification id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountIdentification {
        @JacksonXmlProperty(localName = "IBAN")
        private String iban;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialInstitution {
        @JacksonXmlProperty(localName = "FinInstnId")
        private FinancialInstitutionId financialInstitutionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialInstitutionId {
        @JacksonXmlProperty(localName = "BICFI")
        private String bic;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditTransferTransactionInfo {
        @JacksonXmlProperty(localName = "PmtId")
        private PaymentIdentification paymentId;

        @JacksonXmlProperty(localName = "Amt")
        private Amount amount;

        @JacksonXmlProperty(localName = "CdtrAgt")
        private FinancialInstitution creditorAgent;

        @JacksonXmlProperty(localName = "Cdtr")
        private Party creditor;

        @JacksonXmlProperty(localName = "CdtrAcct")
        private Account creditorAccount;

        @JacksonXmlProperty(localName = "RmtInf")
        private RemittanceInfo remittanceInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentIdentification {
        @JacksonXmlProperty(localName = "EndToEndId")
        private String endToEndId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount {
        @JacksonXmlProperty(localName = "InstdAmt", isAttribute = false)
        private InstructedAmount instructedAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstructedAmount {
        @JacksonXmlProperty(isAttribute = true, localName = "Ccy")
        private String currency;

        @JacksonXmlProperty(localName = "")
        private BigDecimal value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemittanceInfo {
        @JacksonXmlProperty(localName = "Ustrd")
        private String unstructured;
    }
}
