package pro.vhbchieu.telegram_tc_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class TelegramTcBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramTcBotApplication.class, args);
	}

}
