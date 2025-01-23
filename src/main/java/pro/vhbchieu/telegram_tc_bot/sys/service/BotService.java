package pro.vhbchieu.telegram_tc_bot.sys.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public interface BotService {

    /**
     * Handles incoming Telegram commands from an update.
     * <p>
     * This method processes text messages from an update, ignoring non-text messages or
     * messages with text length exceeding `MAX_REQUEST_LENGTH`. Valid commands are delegated
     * to the `commandHandlerFactory`.
     *
     * @param update The incoming Telegram {@link org.telegram.telegrambots.meta.api.objects.Update} object.
     */
    void commandHandle(Update update);

    void callbackHandle(Update update);

    /**
     * Handles incoming Telegram updates. Validates token and timestamp, checks request frequency,
     * then processes voice messages via {@link VoiceService#speechToTextHandle(Update)} and
     * commands via a command handler.
     *
     * @param update The incoming {@link org.telegram.telegrambots.meta.api.objects.Update} object.
     * @param token  The token to validate against the bot's token.
     */
    void updateHandle(Update update, String token);
}
