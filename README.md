# Telegram Chat Bot (Java, Spring Boot 3, MySQL)

Dự án với mục đích làm quen telegram bot và luyện tập sử dụng Java, Spring Boot 3 và MySQL. 
Bot được thiết kế để hỗ trợ người dùng quản lý tài chính cá nhân và gia đình một cách hiệu quả và dễ dàng.

Ý tưởng ban đầu:
*   Bot dễ dùng.
*   Dự án dễ mở rộng.

## Tính năng chính

*   [V1.0:]
    *   Hỗ trợ các lệnh cơ bản (ví dụ: `/start`).
    *   Phản hồi tin nhắn theo từ khóa.
    *   Tích hợp AI Api để hỗ trợ thêm bớt giao dịch dễ dạng (Văn bản tự nhiên, giọng nói)
    *   Có hỗ trợ nút bấm theo tin nhắn để người dùng dễ thao tác.
    *   Lưu trữ lịch sử giao dịch vào cơ sở dữ liệu MySQL.
    *   [Đạng cập nhật...]

## Demo

*   Bạn có thể thử trải nghiệm trước tại [@vhbchieu_tc_bot](https://t.me/vhbchieu_tc_bot)

## Hướng dẫn tự cài đặt

### chuẩn bị

*   Java 17+
*   Python 3.11+
*   Maven 3+
*   MySQL 8+
*   Telegram Bot API Token (Lấy từ [@BotFather](https://t.me/BotFather))
*   Gemini API Token (Có miễn phí tại [Google AI Studio](https://aistudio.google.com/))
*   Azure Speech Service Key (Có miễn phí giới hạn 5h mỗi tháng tại [Microsoft Azure](https://azure.microsoft.com/)). Nếu bạn chạy server Python với local model thì bỏ qua phần này.
*   1 Địa Chỉ Ip Https công khai để làm endpoint cài đặt Telegram Bot Webhook (Nếu không có, tạo đơn giản với [ngrok](https://ngrok.com/))
*   IntelliJ IDEA (Khuyến nghị)

### Cài đặt

1.  **Clone repository:**

    ```bash
    git clone [https://github.com/vhBuiChiHieu/telegram-tc-bot-public.git]
    ```

2.  **cài đặt thư viện cần thiết cho backend python:**
    
    ```bash
    pip install flask pydub azure-cognitiveservices-speech python-dotenv ffmpeg
    ```

3.   **Cấu hình cơ sở dữ liệu:**
    *   Tạo database trong MySQL.
    *   Cập nhật thông tin kết nối database trong file `src/main/resources/application-dev.yml`:

        ```yaml
          datasource:
               username: [MYSQL_USERNAME]
               password: [MYSQL_PASSWORD]
               url: jdbc:mysql://localhost:3306/[MYSQL_DATABASE]?createDatabaseIfNotExist=true
        ```

4.  **Cấu hình Telegram Bot Token:**
    *   Thêm các dòng sau vào `application.properties` hoặc có thể dùng biến môi trường, thay thế các `${NAME}` bằng thông tin của bạn:

        ```properties
        telegram.bot.token=${BOT_TOKEN}
        telegram.bot.username=${BOT_USERNAME}
        telegram.bot.webhook.path=/bot/update/${BOT_TOKEN}
        telegram.url.update=${BOT_URL_GET_UPDATE}
        ai.gemini.token=${GEMINI_TOKEN}
        ai.gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${ai.gemini.token}
        python.stt.endpoint.url=${STT_ENDPOINT}
        ```
    *   Ví dụ về giá trị các tham số:
        ```dotenv
        BOT_TOKEN=AUAUEBEH:ANKDNADKNDNA-NCCNINSIA
        GEMINI_TOKEN=ASNKNknanKSDkKNnkdskaajdn
        BOT_URL_GET_UPDATE=https://abc-xyz-adapted.ngrok-free.app
        AZURE_SPEECH_TO_TEXT_KEY=82bbjdsbJJdsASKpoCNSKEADJJHDSidbaisbaidaibsdiaidb
        STT_ENDPOINT=http://localhost:5000/stt  (endpoit của /transcribe/SpeechToText.py)
        BOT_USERNAME=vhbchieu_tc_bot
        ```

5.  **Mở rộng thêm tính năng:**

    ```
    Chỉ cần tạo 1 class mới implements [CommandHandler]
    Xử lý logic trong handle(Update)
    Phần còn lại Spring sẽ giúp bạn.
    ```

6.  **Chạy ứng dụng:**

    *   Chạy [Ngrok]() nếu dùng.
    *   Chạy backend python:
        ```bash
        py ./transcribe/SpeechToText.py
        ```

    *   Chạy backend java:
        *   Chạy trực tiếp với Intellij IDEA.
        *   Hoặc dùng maven:
            ```bash
            mvn clean install -P dev
            java -jar ./target/[app_name].jar
            ```

## Sử dụng Bot

1.  Tìm kiếm bot của bạn trên Telegram bằng username (đã cấu hình trong `application-secrets.properties`).
2.  Gửi tin nhắn `/start` để bắt đầu.

## Powered By

[<img src="https://static-00.iconduck.com/assets.00/intellij-idea-icon-2048x2026-pt4psh5t.png" width="25" height="25"/>](https://www.google.com/url?sa=E&source=gmail&q=https://www.jetbrains.com/idea/)
[<img src="data:image/png;base64,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" alt="Maven" width="25"/>](https://www.google.com/url?sa=E&source=gmail&q=https://maven.apache.org/)
[<img src="data:image/png;base64,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" alt="Spring" width="25"/>](https://spring.io/)
[<img src="data:image/png;base64,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" alt="MySQL" width="25"/>](https://www.google.com/url?sa=E&source=gmail&q=https://www.mysql.com/)
[<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Telegram_logo.svg/240px-Telegram_logo.svg.png" alt="Telegram" width="25"/>](https://www.google.com/url?sa=E&source=gmail&q=https://telegram.org/)
[<img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" alt="java" width="25"/>](https://www.google.com/url?sa=E&source=gmail&q=https://www.java.com)

-----

