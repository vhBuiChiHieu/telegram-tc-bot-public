package pro.vhbchieu.telegram_tc_bot.config.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class SetCommandListDto {
    private final List<BotCommand> commands;
}
