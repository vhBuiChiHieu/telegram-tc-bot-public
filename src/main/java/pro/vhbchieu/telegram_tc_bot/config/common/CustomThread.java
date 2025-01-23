package pro.vhbchieu.telegram_tc_bot.config.common;

public class CustomThread extends Thread {

    private Long chatId;

    private CustomThread(){
        super();
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
