package pro.vhbchieu.telegram_tc_bot.sys.service.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.sys.service.TextService;
import pro.vhbchieu.telegram_tc_bot.sys.service.UserService;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandHandlerFactory {

    private final TextService textService;
    private final UserService userService;
    private final Map<String, CommandHandler> commandHandlers;

    private static final int NOT_COMMAND_MIN_LENGTH_ALLOWED = 10;

    public CommandHandlerFactory(
            @Autowired List<CommandHandler> commandHandlers,
            @Autowired TextService textService,
            @Autowired UserService userService
    ) {
        this.commandHandlers = commandHandlers.stream().collect(Collectors.toMap(CommandHandler::getCommandName, commandHandler -> commandHandler));
        this.textService = textService;
        this.userService = userService;
        TelegramUtils.setBotCommandList(commandHandlers);
    }

    public void handle(Update update) {
        String messageText = update.getMessage().getText();
        String command = "";

        //text is command or not
        if (!update.getMessage().getText().startsWith("/") && messageText.length() >= NOT_COMMAND_MIN_LENGTH_ALLOWED) {
            //text to command transaction
            String transactionCommand = textService.convertToTransactionCommand(update.getMessage().getText());
            update.getMessage().setText(transactionCommand);
            command = transactionCommand.split(" ")[0];
        } else {
            command = messageText.split(" ")[0].toLowerCase();
        }

        //find command handler
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler != null) {
            if (!command.equals("/start") && !command.equals("/dangki") && userService.getUserIdByTelegramId(update.getMessage().getFrom().getId()) == null) {
                TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "<i>Bạn hãy bấm /dangki để bắt đầu sử dụng bot nhé.</i>", "HTML");
                return;
            }
            commandHandler.handle(update);
        } else {
            TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Xin lỗi, tôi không thể hiểu yêu cầu của bạn.");
        }
    }

    public void callbackQueryHandle(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        String command = callbackData.split("_")[0];
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler != null) {
            commandHandler.handleCallback(update);
        }
    }
}
