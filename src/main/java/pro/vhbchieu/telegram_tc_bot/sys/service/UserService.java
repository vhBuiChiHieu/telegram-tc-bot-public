package pro.vhbchieu.telegram_tc_bot.sys.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import pro.vhbchieu.telegram_tc_bot.sys.entity.User;

@Service
public interface UserService {

    User createUser(User newUser);

    Long getUserIdByTelegramId(@NonNull Long id);

}
