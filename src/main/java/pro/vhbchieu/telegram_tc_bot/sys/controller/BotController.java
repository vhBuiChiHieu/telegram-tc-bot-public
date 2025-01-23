package pro.vhbchieu.telegram_tc_bot.sys.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.sys.service.BotService;

@RestController
@RequestMapping("/bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;
    private final ThreadPoolTaskExecutor taskExecutor;

    @PostMapping("/update/{token}")
    public ResponseEntity<Void> update(@PathVariable(name = "token") String token, @RequestBody Update update) {
        if ((update.hasMessage() && (update.getMessage().hasVoice() || update.getMessage().hasText())) || update.hasCallbackQuery())
            taskExecutor.submit(() -> botService.updateHandle(update, token));
        return ResponseEntity.ok().build();
    }
}
