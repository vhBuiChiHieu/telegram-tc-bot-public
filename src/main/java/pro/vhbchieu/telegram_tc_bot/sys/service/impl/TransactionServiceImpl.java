package pro.vhbchieu.telegram_tc_bot.sys.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.vhbchieu.telegram_tc_bot.config.common.PageDto;
import pro.vhbchieu.telegram_tc_bot.config.constant.TransactionType;
import pro.vhbchieu.telegram_tc_bot.exception.ClientException;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;
import pro.vhbchieu.telegram_tc_bot.sys.repository.TransactionRepository;
import pro.vhbchieu.telegram_tc_bot.sys.repository.UserRepository;
import pro.vhbchieu.telegram_tc_bot.sys.service.TransactionService;
import pro.vhbchieu.telegram_tc_bot.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public Transaction create(Long userId, Transaction newTransaction) {
        newTransaction.setUser(userRepository.findById(userId).orElseThrow(() -> new ClientException(14, "Người dùng " + userId + " chưa có trên hệ thống.")));
        return transactionRepository.save(newTransaction);
    }

    @Override
    public Transaction createByTelegramId(@NonNull Long id, Transaction newTransaction) {
        Long userId = userRepository.getUserIdByTelegramId(id.toString());
        return create(userId, newTransaction);
    }

    @Override
    public void deleteLatest() {
        Transaction transaction = transactionRepository.findFirstByOrderByTransactionDateDesc();
        if (transaction != null)
            transactionRepository.delete(transaction);
    }

    @Override
    public void deleteAll(Long userId) {
        transactionRepository.deleteTransactionByUserId(userId);
    }

    @Override
    public String formatTransaction(Transaction transaction) {
        String prefix = "";
        if (transaction.getTransactionType() == TransactionType.INCOMING)
            prefix = "+";
        else
            prefix = "-";
        String amount = prefix + "%,d".formatted(transaction.getAmount());
        return  new StringBuilder().append("<b>Thay Đổi:</b> <code>").append(amount).append("</code> <b>VND</b>\n")
                .append("<b>Mô Tả:</b> ").append(transaction.getDescription() != null ? transaction.getDescription() : "No description").append("\n")
                .append("<b>Thời Gian:</b> ").append(DateUtils.formatDate(transaction.getTransactionDate(), " dd/MM/yyyy HH:mm:ss")).append("\n")
                .toString();
    }

    @Override
    public List<Transaction> getTransactionsByTelegramId(@NonNull Long telegramId) {
        return transactionRepository.getTransactionsByTelegramId(telegramId);
    }

    @Override
    public PageDto<List<Transaction>> getPage(int pageNo, int pageSize, Long userId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactions = transactionRepository.findAllByUser_IdOrderByTransactionDateDesc(userId, pageable);
        return PageDto.<List<Transaction>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(transactions.getTotalPages())
                .pageItems(transactions.getContent())
                .build();
    }

    @Override
    public PageDto<List<Transaction>> getPage(int pageNo, int pageSize, Long userId, LocalDateTime from, LocalDateTime to) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Transaction> transactions = transactionRepository.findByUser_IdAndTransactionDateBetween(userId, from, to, pageable);
        return  PageDto.<List<Transaction>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(transactions.getTotalPages())
                .pageItems(transactions.getContent())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSumAmountByUser_IdAndTransactionDateAndType(Long userId, String transactionDate, TransactionType type) {
        Optional<Long> result = transactionRepository.getSumAmountByUser_IdAndTransactionDateAndType(userId, transactionDate, type);
        if (result.isPresent())
            return result.get();
        log.warn("optional getSumAmountByUser_IdAndTransactionDateAndType blank");
        return 0;
    }

    @Override
    public long getSumAmountByUser_Id(Long userId, TransactionType transactionType) {
        Optional<Long> result = transactionRepository.getSumAmountByUser_IdAndTransactionType(userId, transactionType);
        if (result.isPresent())
            return result.get();
        return 0;
    }

    //yyyy-mm
    @Override
    public long getSumAmountByUser_IdAndTransactionMonthAndType(Long userId, String transactionMonth, TransactionType type) {
        Optional<Long> result = transactionRepository.getSumAmountByUser_IdAndTransactionMonthAndType(userId, transactionMonth, type);
        if (result.isPresent())
            return result.get();
        return 0;
    }

}
