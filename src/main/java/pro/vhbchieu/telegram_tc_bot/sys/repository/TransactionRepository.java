package pro.vhbchieu.telegram_tc_bot.sys.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pro.vhbchieu.telegram_tc_bot.config.constant.TransactionType;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findFirstByOrderByTransactionDateDesc();

    @Modifying
    @Transactional
    @Query("delete from Transaction t where t.user.id = :userId")
    void deleteTransactionByUserId(Long userId);

    @Query("select t from Transaction t where t.user.telegramId = :telegramId order by t.transactionDate desc")
    List<Transaction> getTransactionsByTelegramId(@NonNull Long telegramId);

    Page<Transaction> findAllByUser_IdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    Transaction getTransactionById(Long id);

    @Query("select t from Transaction t where t.user.id = :userId and (t.transactionDate between :from and :to) order by t.transactionDate desc")
    Page<Transaction> findByUser_IdAndTransactionDateBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query("select sum(t.amount) from Transaction t where (t.user.id = :userId and function('DATE', t.transactionDate) = :transactionDate) and t.transactionType = :type ")
    Optional<Long> getSumAmountByUser_IdAndTransactionDateAndType(Long userId, String transactionDate, TransactionType type);   //yyyy-MM-dd

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
            "t.user.id = :userId AND " +
            "FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m') = :transactionMonth AND " +
            "t.transactionType = :type")
    Optional<Long> getSumAmountByUser_IdAndTransactionMonthAndType(Long userId, String transactionMonth, TransactionType type); //yyyy-MM

    @Query("select sum(t.amount) from Transaction t where t.user.id = :userId and t.transactionType = :transactionType")
    Optional<Long> getSumAmountByUser_IdAndTransactionType(Long userId, TransactionType transactionType);
}
