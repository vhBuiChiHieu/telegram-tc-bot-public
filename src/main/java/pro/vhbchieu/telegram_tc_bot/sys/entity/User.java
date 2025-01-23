package pro.vhbchieu.telegram_tc_bot.sys.entity;

import jakarta.persistence.*;
import lombok.*;
import pro.vhbchieu.telegram_tc_bot.config.constant.SaveType;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@ToString
public class User extends AbstractEntity{

    @Column(name = "chat_id", unique = true, nullable = false)
    private String chatId;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private String telegramId;

    @Column(name = "username" , unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "save_type")
    private SaveType saveType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private Set<Transaction> transactions;
}
