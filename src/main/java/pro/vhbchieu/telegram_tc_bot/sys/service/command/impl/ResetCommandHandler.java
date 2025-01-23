package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.sys.service.TransactionService;
import pro.vhbchieu.telegram_tc_bot.sys.service.UserService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetCommandHandler implements CommandHandler {

    private final TransactionService transactionService;
    private final UserService userService;

    @Override
    public String getCommandName() {
        return "/xoahet";
    }

    @Override
    public String getCommandDescription() {
        return "Xóa tất cả giao dịch của người dùng";
    }

    @Override
    public void handle(Update update) {
        Long userId = userService.getUserIdByTelegramId(update.getMessage().getFrom().getId());
        transactionService.deleteAll(userId);
        TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Đã xóa toàn bộ giao dịch");
    }

    @Override
    public void handleCallback(Update update) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
