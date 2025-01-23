package pro.vhbchieu.telegram_tc_bot.sys.service;

import lombok.NonNull;
import pro.vhbchieu.telegram_tc_bot.config.common.PageDto;
import pro.vhbchieu.telegram_tc_bot.config.constant.TransactionType;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Transaction create(Long userId, Transaction newTransaction);

    Transaction createByTelegramId(@NonNull Long id, Transaction newTransaction);

    void deleteLatest();

    void deleteAll(Long userId);

    String formatTransaction(Transaction transaction);

    List<Transaction> getTransactionsByTelegramId(@NonNull Long telegramId);

    PageDto<List<Transaction>> getPage(int pageNo, int pageSize, Long userId);

    PageDto<List<Transaction>> getPage(int pageNo, int pageSize, Long userId, LocalDateTime from, LocalDateTime to);

    /**
     * Get sum amount with transaction date and type
     * @param userId transaction user_id
     * @param transactionDate String yyyy/MM/dd
     * @param type TransactionType
     * @return {@code sum}
     */
    long getSumAmountByUser_IdAndTransactionDateAndType(Long userId, String transactionDate, TransactionType type);

    long getSumAmountByUser_Id(Long userId, TransactionType transactionType);

    long getSumAmountByUser_IdAndTransactionMonthAndType(Long userId, String transactionMonth, TransactionType type);
}
