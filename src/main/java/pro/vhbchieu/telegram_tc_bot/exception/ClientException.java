package pro.vhbchieu.telegram_tc_bot.exception;

import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {
    private final int statusCode;

    public ClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
