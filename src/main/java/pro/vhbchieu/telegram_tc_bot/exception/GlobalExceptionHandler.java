package pro.vhbchieu.telegram_tc_bot.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;
import pro.vhbchieu.telegram_tc_bot.utils.ValidateUtils;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Void> handleException(final Exception e, HttpServletRequest request) {
        log.error("Exception: {}", e.getMessage(), e);
//        String chatId = request.getAttribute("chatId").toString();
//        TelegramUtils.sendMessage(chatId, "Xin lỗi, tôi hiện tại không thể xử lý yêu cầu này.");
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = ClientException.class)
    ResponseEntity<Void> handleClientException(ClientException e, HttpServletRequest request) {
        log.error("ClientException: {}", e.getMessage());
//        String chatId = request.getAttribute("chatId").toString();
//        TelegramUtils.sendMessage(chatId, e.getMessage());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.error("NoResourceFoundException: {}", e.getMessage());
        return ResponseEntity.ok().build();
    }
}
