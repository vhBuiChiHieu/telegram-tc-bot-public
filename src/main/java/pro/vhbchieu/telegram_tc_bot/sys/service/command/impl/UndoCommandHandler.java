package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.sys.service.TransactionService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class UndoCommandHandler implements CommandHandler {

    private final TransactionService transactionService;

    @Override
    public String getCommandName() {
        return "/hoantac";
    }

    @Override
    public String getCommandDescription() {
        return "Hủy bỏ 1 giao dịch gần nhất";
    }

    @Override
    public void handle(Update update) {
        transactionService.deleteLatest();
        TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Đã xóa giao dịch gần nhất của " + update.getMessage().getFrom().getUserName());
    }

    @Override
    public void handleCallback(Update update) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
