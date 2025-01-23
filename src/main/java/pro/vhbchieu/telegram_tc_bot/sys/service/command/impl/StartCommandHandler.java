package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

@Component
public class StartCommandHandler implements CommandHandler {
    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public String getCommandDescription() {
        return "Xem hướng dẫn và danh sách lệnh.";
    }

    @Override
    public void handle(Update update) {
        String sendMessage = """
               💬    <b>Xin chào tôi là Bot quản lí chi tiêu cá nhân và gia đình</b>    💬
                
                <i>Nếu đây là lần đầu bạn sử dụng bot, bấm /dangki để bắt đầu sử dụng các tính năng nhé nhé</i>!
                
                <b>Hướng dẫn sử dụng📚:</b>
                
                <b>1. Thêm giao dịch mới:</b>
                <code>/thu [số_tiền] [mô_tả]</code> : thêm khoản thu nhập mới.
                    ví dụ: <i>/thu 500000 tiền thưởng</i>
                    
                <code>/chi [số_tiền] [mô_tả]</code> : thêm khoản chi mới.
                    ví dụ: <i>/chi 40000 mua thị gà</i>
                    
                Nhập (hoặc voice) 1 câu mô tả bất kì nhưng rõ dàng:
                    ví dụ: 
                        <i>đi chợ hết 50k.</i>
                        <i>nay vừa có 5tr bán bưởi.</i>
                            
                <b>2. Hoàn tác giao dịch:</b>
                <code>/hoantac</code> : Xóa bỏ 1 giao dịch gần nhất.
                
                <b>3. Cài lại dữ liệu:</b>
                <code>/xoahet</code> : Xóa toàn bộ giao dịch của tài khoản.
                
                <b>4. Báo Cáo:</b>
                <code>/baocao [dd/MM/yyyy]</code> : Hiện thị toàn bộ giao dịch hoặc tùy chỉnh.
                    ví dụ: 
                        <i>/baocao</i>
                        <i>/baocao 05/2024</i>
                        <i>/baocao 16/09/2024</i>
                
               <i>Nếu bạn cần xem lại hãy bấm /start nhé!</i>
                """;
        TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), sendMessage, "HTML");
    }

    @Override
    public void handleCallback(Update update) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
