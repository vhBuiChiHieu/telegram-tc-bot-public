package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.config.constant.SaveType;
import pro.vhbchieu.telegram_tc_bot.sys.entity.User;
import pro.vhbchieu.telegram_tc_bot.sys.service.UserService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;
import pro.vhbchieu.telegram_tc_bot.utils.DateUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterCommandHandler implements CommandHandler {

    private final UserService userService;

    @Override
    public String getCommandName() {
        return "/dangki";
    }

    @Override
    public String getCommandDescription() {
        return "Đăng kí để bắt đầu sử dụng các chức năng của bot";
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
            User createdUser = userService.createUser(
                    User.builder()
                            .chatId(message.getChatId().toString())
                            .telegramId(message.getFrom().getId().toString())
                            .username(message.getFrom().getUserName())
                            .firstName(message.getFrom().getFirstName())
                            .lastName(message.getFrom().getLastName())
                            .saveType(SaveType.DATE_BASE)
                            .build()
            );
            String sendMessage = "Chúc mừng bạn đã đăt kí thành công!" +
                    "\nThời gian đăng kí: " + DateUtils.formatDate(createdUser.getCreatedAt());

            TelegramUtils.sendMessage(message.getChatId().toString(), sendMessage);
    }

    @Override
    public void handleCallback(Update update) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

