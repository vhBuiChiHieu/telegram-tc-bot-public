package pro.vhbchieu.telegram_tc_bot.sys.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.vhbchieu.telegram_tc_bot.exception.ClientException;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;
import pro.vhbchieu.telegram_tc_bot.sys.entity.User;
import pro.vhbchieu.telegram_tc_bot.sys.repository.TransactionRepository;
import pro.vhbchieu.telegram_tc_bot.sys.repository.UserRepository;
import pro.vhbchieu.telegram_tc_bot.sys.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public User createUser(User newUser) {
        if (userRepository.existsUserByUsername(newUser.getUsername()))
            throw new ClientException(11, "Người dùng " + newUser.getUsername() + " đã đăng kí trước đây rồi.");
        return userRepository.save(newUser);
    }

    @Override
    public Long getUserIdByTelegramId(@NonNull Long id) {
        return userRepository.getUserIdByTelegramId(id.toString());
    }

}
