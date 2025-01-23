package pro.vhbchieu.telegram_tc_bot.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.vhbchieu.telegram_tc_bot.sys.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);

    @Query("select u.id from User u where u.telegramId = :telegramId")
    Long getUserIdByTelegramId(String telegramId);
}
