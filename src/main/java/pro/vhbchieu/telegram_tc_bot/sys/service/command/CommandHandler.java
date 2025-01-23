package pro.vhbchieu.telegram_tc_bot.sys.service.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    String getCommandName();
    String getCommandDescription();
    void handle(Update update);
    void handleCallback(Update update);
}
