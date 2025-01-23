package pro.vhbchieu.telegram_tc_bot.config.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageDto<T> {
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private T pageItems;
}
