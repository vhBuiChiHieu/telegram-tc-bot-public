package pro.vhbchieu.telegram_tc_bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ValidateUtils {

    public static final int REPORT_INVALID = 0;

    public static final int REPORT_DAY = 1;

    public static final int REPORT_MONTH = 2;

    private static final ConcurrentHashMap<Long, Integer> lastMessageTime = new ConcurrentHashMap<>();

    private static final String CHANGE_AMOUNT_PATTEN = "/[a-zA-Z]+ \\d+(\\.\\d+)? .+";

    private static final String REPORT_CALLBACK_QUERY_PATTEN = "^/baocao_(previous|next)_(\\d+)_(\\d+)(.*)$";

    private static final String REPORT_DAY_PATTEN = "^/baocao (\\d{2})/(\\d{2})/(\\d{4})$";

    private static final String REPORT_MONTH_PATTEN = "^/baocao (\\d{2})/(\\d{4})$";

    private static final int MIN_TIME_BETWEEN_MESSAGES = 7;

    private ValidateUtils() {}

    public static boolean isValidChangeAmount(String input) {
        Pattern pattern = Pattern.compile(CHANGE_AMOUNT_PATTEN);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static int getReportMatched(String input) {
        Pattern pattern = Pattern.compile(REPORT_DAY_PATTEN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches())
            return REPORT_DAY;
        pattern = Pattern.compile(REPORT_MONTH_PATTEN);
        matcher = pattern.matcher(input);
        if (matcher.matches())
            return REPORT_MONTH;
        return REPORT_INVALID;
    }

    public static int[] getReportParams(String input) {
        int[] params = new int[3];
        Pattern pattern = Pattern.compile(REPORT_DAY_PATTEN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()){
            params[0] = Integer.parseInt(matcher.group(1));
            params[1] = Integer.parseInt(matcher.group(2));
            params[2] = Integer.parseInt(matcher.group(3));
            return params;
        }
        pattern = Pattern.compile(REPORT_MONTH_PATTEN);
        matcher = pattern.matcher(input);
        if (matcher.matches()){
            params[0] = Integer.parseInt(matcher.group(1));
            params[1] = Integer.parseInt(matcher.group(2));
        }
        return params;
    }



    public static List<String> getReportParamsCallbackQuery(String input) {
        List<String> params = new ArrayList<>();
        Pattern pattern = Pattern.compile(REPORT_CALLBACK_QUERY_PATTEN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            params.add(matcher.group(1));
            params.add(matcher.group(2));
            params.add(matcher.group(3));
            params.add(matcher.group(4));
        }
        return params;
    }

    public static boolean isRequestTooFast(Long telegramId, Integer sendTime){
        Integer lastSend = lastMessageTime.putIfAbsent(telegramId, sendTime);
        if ( lastSend == null)
            return true;

        if (sendTime - lastSend > MIN_TIME_BETWEEN_MESSAGES){
            lastMessageTime.put(telegramId, sendTime);
            return true;
        }
        return false;
    }


}
