package pro.vhbchieu.telegram_tc_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

@Configuration
public class InitConfig {

    @Value("${telegram.bot.token}")
    String botToken;

    @Value("${telegram.bot.webhook.path}")
    String webhookPath;

    @Value("${telegram.bot.username}")
    String botUsername;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    TelegramWebhookBot telegramWebhookBot() {
        TelegramWebhookBot webhookBot =  new TelegramWebhookBot(botToken) {
            @Override
            public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
                //dont need if using Webhook
                return null;
            }

            @Override
            public String getBotPath() {
                return webhookPath;
            }

            @Override
            public String getBotUsername() {
                return botUsername;
            }
        };

        TelegramUtils.setWebhookBot(webhookBot);
        return webhookBot;
    }
}
