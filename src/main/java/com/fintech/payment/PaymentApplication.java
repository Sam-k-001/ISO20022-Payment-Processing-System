// PaymentApplication.java
package com.fintech.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ISO-20022 Payment Processing System
 *
 * <p>Production-grade FinTech payment microservice implementing ISO-20022
 * messaging standards for payment initiation (pain.001), interbank settlement
 * (pacs.008), and account reporting (camt.053).</p>
 *
 * @author FinTech Engineering Team
 * @version 1.0.0-RELEASE
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}

