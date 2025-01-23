package pro.vhbchieu.telegram_tc_bot.sys.service;

import org.springframework.stereotype.Service;

@Service
public interface TextService {

    String convertToTransactionCommand(String input);

}
