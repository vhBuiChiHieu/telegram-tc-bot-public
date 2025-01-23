package pro.vhbchieu.telegram_tc_bot.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtils {

    private DateUtils() {}

    public static String formatDate(Date date, String... newPattern) {
        String pattern = newPattern.length > 0 ? newPattern[0] : "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static boolean isRequestOutDate(Integer unixTime, int maxAllowedTimeDifference) {
        return (System.currentTimeMillis() / 1000) - unixTime > maxAllowedTimeDifference;
    }

    public static String formatDateString(String input, String inputPattern, String outputPattern) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputPattern);

        LocalDate date = LocalDate.parse(input, inputFormatter);

        return date.format(outputFormatter);
    }

    public static String mmYyyyToYyyyMm(String input) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth yearMonth = YearMonth.parse(input, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            return yearMonth.format(outputFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Lỗi: Định dạng không hợp lệ!");
        }
        return "";
    }
}
