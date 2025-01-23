package pro.vhbchieu.telegram_tc_bot.sys.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.vhbchieu.telegram_tc_bot.sys.service.BotService;
import pro.vhbchieu.telegram_tc_bot.sys.service.VoiceService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandlerFactory;
import pro.vhbchieu.telegram_tc_bot.utils.DateUtils;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;
import pro.vhbchieu.telegram_tc_bot.utils.ValidateUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    @Value("${telegram.bot.webhook.path}")
    private String webhookPath;

    @Value("${telegram.url.update}")
    private String updateDomain;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramWebhookBot webhookBot;
    private final CommandHandlerFactory commandHandlerFactory;
    private final VoiceService voiceService;

    private static final int MAX_ALLOWED_TIME_DIFFERENCE = 60;
    private static final int MAX_REQUEST_LENGTH = 50;


    @PostConstruct
    public void registerWebhook() {
        try {
            SetWebhook setWebhook = new SetWebhook();   //method
            setWebhook.setUrl(updateDomain + webhookPath);
            webhookBot.execute(setWebhook);
            log.info(webhookBot.getWebhookInfo().toString());
        } catch (TelegramApiException e){
            log.error("registerWebhook error: {}, domain: {}, webhookPath: {}", e.getMessage(), updateDomain, webhookPath);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void commandHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        if (update.getMessage().getText().length() <= MAX_REQUEST_LENGTH) {
            commandHandlerFactory.handle(update);
        } else {
            TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Xin lỗi, tôi không thể hiểu các yêu cầu quá dài (" + MAX_REQUEST_LENGTH + " kí tự). \nXin vui lòng thử lại!");
        }
    }

    @Override
    public void callbackHandle(Update update) {
        commandHandlerFactory.callbackQueryHandle(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateHandle(Update update, String token) {
        //check token
        if (!token.equals(botToken))
            return;

        if (update.hasCallbackQuery()) {
            callbackHandle(update);
        } else {
            if (DateUtils.isRequestOutDate(update.getMessage().getDate(), MAX_ALLOWED_TIME_DIFFERENCE))
                return;

            if (!ValidateUtils.isRequestTooFast(update.getMessage().getFrom().getId(), update.getMessage().getDate())) {
                TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Vui lòng không nhắn quá nhanh.");
                return;
            }

            voiceService.speechToTextHandle(update);
            commandHandle(update);
        }
    }

}
