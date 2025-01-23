package pro.vhbchieu.telegram_tc_bot.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TelegramUtils {

    private static TelegramWebhookBot webhookBot;

    private TelegramUtils() {}

    @SneakyThrows
    public static void sendMessage(String chatId, String message, String... parseMode) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (parseMode != null && parseMode.length > 0) {
            sendMessage.setParseMode(parseMode[0]);
        }
        webhookBot.execute(sendMessage);
    }

    public static void sendInvalidCommand(String chatId, String... message) {
        sendMessage(chatId, "Xin Lỗi, yêu cầu của bạn không hợp lệ, hãy kiểm tra lại.\n" + String.join(" ", message));
    }

    public static void setWebhookBot(TelegramWebhookBot webhookBot) {
        TelegramUtils.webhookBot = webhookBot;
    }

    /**
     * Automatically create a command list for the bot scale with the number of CommandHandler implementations
     * @param commandHandlers list CommandHandler implements
     */
    @SneakyThrows
    public static void setBotCommandList(List<CommandHandler> commandHandlers) {
        List<BotCommand> botCommands = new ArrayList<>();
        for (CommandHandler commandHandler : commandHandlers) {
            BotCommand botCommand = new BotCommand();
            botCommand.setCommand(commandHandler.getCommandName());
            botCommand.setDescription(commandHandler.getCommandDescription());
            botCommands.add(botCommand);
        }
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(botCommands);
        webhookBot.executeAsync(setMyCommands);
    }
}
