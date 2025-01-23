package pro.vhbchieu.telegram_tc_bot.sys.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public interface VoiceService {
    void speechToTextHandle(Update update);
}
