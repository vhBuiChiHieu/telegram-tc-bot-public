package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.config.constant.TransactionType;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;
import pro.vhbchieu.telegram_tc_bot.sys.service.TransactionService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;
import pro.vhbchieu.telegram_tc_bot.utils.DateUtils;
import pro.vhbchieu.telegram_tc_bot.utils.ValidateUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpenseCommandHandler implements CommandHandler {

    private final TransactionService transactionService;

    @Override
    public String getCommandName() {
        return "/chi";
    }

    @Override
    public String getCommandDescription() {
        return "Thêm 1 giao dịch chi tiền mới. /chi <số tiền> <mô tả>";
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        if (ValidateUtils.isValidChangeAmount(message.getText())) {
            String[] params = message.getText().split(" ", 3);
            String sendMessage = """
                    Người Dùng <code>%s</code>
                    Thay Đổi: <b>-%,d</b> VND 💰
                    Mô Tả: %s.
                    Ngày: %s
                    """;

            Transaction newTransaction = Transaction.builder()
                    .amount(Long.parseLong(params[1]))
                    .description(params[2])
                    .transactionType(TransactionType.EXPENSE)
                    .transactionDate(new Date())
                    .build();
            Transaction createdTransaction = transactionService.createByTelegramId(update.getMessage().getFrom().getId(), newTransaction);

            sendMessage = sendMessage.formatted(
                    createdTransaction.getUser().getUsername(),
                    createdTransaction.getAmount(),
                    createdTransaction.getDescription(),
                    DateUtils.formatDate(createdTransaction.getTransactionDate(),"dd/MM/yyyy"));
            TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), sendMessage, "HTML");
        } else
            TelegramUtils.sendInvalidCommand(message.getChatId().toString());
    }

    @Override
    public void handleCallback(Update update) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
