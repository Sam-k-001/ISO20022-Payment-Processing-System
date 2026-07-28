// PaymentTransactionRepository.java
package com.fintech.payment.repository;

import com.fintech.payment.domain.entity.PaymentTransaction;
import com.fintech.payment.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for PaymentTransaction persistence.
 *
 * <p>Follows the Repository pattern to abstract database operations
 * from business logic, enabling easy substitution of persistence providers.</p>
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    /**
     * Retrieves a transaction by its unique end-to-end identifier.
     * Used for idempotency checks on duplicate submission prevention.
     */
    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    /**
     * Checks for duplicate transaction submissions using idempotency key.
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Retrieves paginated payment history for admin reporting.
     */
    Page<PaymentTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Retrieves all transactions by payment status for processing queues.
     */
    List<PaymentTransaction> findByStatus(PaymentStatus status);

    /**
     * Finds all transactions for a specific debtor account.
     */
    Page<PaymentTransaction> findByDebtorAccount(String debtorAccount, Pageable pageable);

    /**
     * Finds all transactions for a specific creditor account.
     */
    Page<PaymentTransaction> findByCreditorAccount(String creditorAccount, Pageable pageable);

    /**
     * Aggregates total settled amount per currency — used for reconciliation reports.
     */
    @Query("""
        SELECT p.currency, SUM(p.amount) as totalAmount, COUNT(p) as count
        FROM PaymentTransaction p
        WHERE p.status = :status
        GROUP BY p.currency
        """)
    List<Object[]> sumAmountByCurrencyAndStatus(@Param("status") PaymentStatus status);

    /**
     * Retrieves transactions within a date range — useful for batch reconciliation.
     */
    @Query("""
        SELECT p FROM PaymentTransaction p
        WHERE p.createdAt BETWEEN :startDate AND :endDate
        AND (:status IS NULL OR p.status = :status)
        ORDER BY p.createdAt DESC
        """)
    Page<PaymentTransaction> findByDateRangeAndStatus(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("status") PaymentStatus status,
        Pageable pageable
    );

    /**
     * Bulk status update for batch settlement processing.
     * Uses JPQL bulk update to avoid loading all entities into memory.
     */
    @Modifying
    @Query("""
        UPDATE PaymentTransaction p
        SET p.status = :newStatus, p.updatedAt = :updatedAt
        WHERE p.status = :currentStatus
        AND p.createdAt < :cutoffTime
        """)
    int bulkUpdateStatus(
        @Param("currentStatus") PaymentStatus currentStatus,
        @Param("newStatus") PaymentStatus newStatus,
        @Param("cutoffTime") LocalDateTime cutoffTime,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    /**
     * Finds high-value transactions above threshold — used by fraud detection.
     */
    @Query("SELECT p FROM PaymentTransaction p WHERE p.amount >= :threshold ORDER BY p.amount DESC")
    List<PaymentTransaction> findHighValueTransactions(@Param("threshold") BigDecimal threshold);
}
