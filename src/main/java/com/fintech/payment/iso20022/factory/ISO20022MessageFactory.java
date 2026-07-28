// ISO20022MessageFactory.java
package com.fintech.payment.iso20022.factory;

import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.iso20022.generator.Camt053Generator;
import com.fintech.payment.iso20022.generator.Pacs008Generator;
import com.fintech.payment.iso20022.generator.Pain001Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory class that orchestrates ISO-20022 message generation.
 *
 * <p>Implements the Factory pattern to decouple message creation from
 * business logic. Each payment type generates a specific message chain:
 * pain.001 → pacs.008 → camt.053</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ISO20022MessageFactory {

    private final Pain001Generator pain001Generator;
    private final Pacs008Generator pacs008Generator;
    private final Camt053Generator camt053Generator;

    /**
     * Generates the complete ISO-20022 message chain for a payment transaction.
     *
     * @param transaction The persisted payment transaction
     * @return MessageChain containing all generated XML messages
     */
    public MessageChain generateMessageChain(PaymentTransaction transaction) {
        log.info("Generating ISO-20022 message chain for transaction: {}", transaction.getTransactionId());

        String pain001Xml = pain001Generator.generate(transaction);
        String pacs008Xml = pacs008Generator.generate(transaction);
        String camt053Xml = camt053Generator.generate(transaction);

        log.debug("ISO-20022 message chain generated successfully for: {}", transaction.getTransactionId());

        return new MessageChain(pain001Xml, pacs008Xml, camt053Xml);
    }

    /** Value object containing the complete ISO-20022 XML message chain */
    public record MessageChain(
        String pain001Xml,
        String pacs008Xml,
        String camt053Xml
    ) {}
}
