package pro.vhbchieu.telegram_tc_bot.sys.service.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pro.vhbchieu.telegram_tc_bot.config.common.PageDto;
import pro.vhbchieu.telegram_tc_bot.config.constant.TransactionType;
import pro.vhbchieu.telegram_tc_bot.sys.entity.Transaction;
import pro.vhbchieu.telegram_tc_bot.sys.service.TransactionService;
import pro.vhbchieu.telegram_tc_bot.sys.service.UserService;
import pro.vhbchieu.telegram_tc_bot.sys.service.command.CommandHandler;
import pro.vhbchieu.telegram_tc_bot.utils.DateUtils;
import pro.vhbchieu.telegram_tc_bot.utils.ValidateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportCommandHandler implements CommandHandler {

    private final TransactionService transactionService;
    private final UserService userService;
    private final TelegramWebhookBot webhookBot;

    @Override
    public String getCommandName() {
        return "/baocao";
    }

    @Override
    public String getCommandDescription() {
        return "Báo cáo lại tất cả các giao dịch và thống kê theo tùy chỉnh";
    }

    @Override
    public void handle(Update update) {
        String text = update.getMessage().getText();
        int pageSize = 6;

        //Xử lý tham số nếu có
        PageDto<List<Transaction>> pageTransaction;
        if (ValidateUtils.getReportMatched(text) == ValidateUtils.REPORT_INVALID) {
            pageTransaction = getPageTransaction(0, pageSize, update.getMessage().getFrom().getId());
            sendReport(pageTransaction, update.getMessage().getChatId(), update.getMessage().getFrom().getUserName());
        } else {
            String reportTimeString = update.getMessage().getText().split(" ")[1];
            pageTransaction = getPageTransaction(0, pageSize, update.getMessage().getFrom().getId(), reportTimeString);
            sendReport(pageTransaction, update.getMessage().getChatId(), update.getMessage().getFrom().getUserName(), reportTimeString);
        }
    }

    @SneakyThrows
    @Override
    public void handleCallback(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        log.info("CallBackData: {}", callBackData);
        List<String> params = ValidateUtils.getReportParamsCallbackQuery(callBackData);

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        if (callBackData.equals("/baocao_delete")) {
            deleteMessage(chatId, messageId);
        } else if (!params.isEmpty()) {
            String move = params.get(0);
            int pageNo = Integer.parseInt(params.get(1));
            int pageSide = Integer.parseInt(params.get(2));
            String dateTime = params.get(3).replace("_", "");

            if (move.equals("next")) {
                PageDto<List<Transaction>> pageTransaction = getPageTransaction(pageNo + 1, pageSide, update.getCallbackQuery().getFrom().getId(), dateTime);
                editReport(pageTransaction, chatId, messageId, dateTime);
            } else if (move.equals("previous")) {
                PageDto<List<Transaction>> pageTransaction = getPageTransaction(pageNo - 1, pageSide, update.getCallbackQuery().getFrom().getId(), dateTime);
                editReport(pageTransaction, chatId, messageId, dateTime);
            }

        }
    }

    @SneakyThrows
    private void sendReport(PageDto<List<Transaction>> pageTransaction, Long chatId, String username, String... dateTime) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());

        String seedMessageText = "";
        List<Transaction> transactions = pageTransaction.getPageItems();

        if (!transactions.isEmpty()) {
            long sumIncome;
            long sumExpense;
            if (dateTime != null && dateTime.length > 0 && !dateTime[0].isEmpty()) {
                if (dateTime[0].split("/").length == 3){
                    sumIncome = transactionService.getSumAmountByUser_IdAndTransactionDateAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.formatDateString(dateTime[0], "dd/MM/yyyy", "yyyy-MM-dd"), TransactionType.INCOMING);
                    sumExpense = transactionService.getSumAmountByUser_IdAndTransactionDateAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.formatDateString(dateTime[0], "dd/MM/yyyy", "yyyy-MM-dd"), TransactionType.EXPENSE);
                } else {
                    sumIncome = transactionService.getSumAmountByUser_IdAndTransactionMonthAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.mmYyyyToYyyyMm(dateTime[0]), TransactionType.INCOMING);
                    sumExpense = transactionService.getSumAmountByUser_IdAndTransactionMonthAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.mmYyyyToYyyyMm(dateTime[0]), TransactionType.EXPENSE);
                }
            } else {
                sumIncome = transactionService.getSumAmountByUser_Id(transactions.get(0).getUser().getId(), TransactionType.INCOMING);
                sumExpense = transactionService.getSumAmountByUser_Id(transactions.get(0).getUser().getId(), TransactionType.EXPENSE);
            }
            seedMessageText = formatTransactions(pageTransaction, sumIncome, sumExpense);
        }

        if (seedMessageText.isEmpty()) {
            seedMessageText = "Người dùng " + username + " chưa có giao dịch nào.";
        } else {
            sendMessage.setReplyMarkup(getKeyboard(pageTransaction.getPageNo(), pageTransaction.getPageSize(), pageTransaction.getTotalPages(), dateTime));
        }

        sendMessage.setText(seedMessageText);
        sendMessage.setParseMode("HTML");
        webhookBot.execute(sendMessage);
    }

    private InlineKeyboardMarkup getKeyboard(int pageNo, int pageSize, int totalPages, String... dateTime) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        //row
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> buttonsInline1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonsInline3 = new ArrayList<>();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();

        String dateQuery;
        if (dateTime == null || dateTime.length == 0 || dateTime[0].isEmpty()) {
            dateQuery = "";
        } else {
            dateQuery = "_" + dateTime[0];
        }

        if (pageNo > 0) {
            InlineKeyboardButton preButton = new InlineKeyboardButton();
            preButton.setText("⏪ Trước");
            preButton.setCallbackData("/baocao_previous_" + pageNo + "_" + pageSize + dateQuery);
            buttonsInline1.add(preButton);
        }
        if (pageNo < totalPages - 1) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            nextButton.setText("Sau ⏩");
            nextButton.setCallbackData("/baocao_next_" + pageNo + "_" + pageSize + dateQuery);
            buttonsInline1.add(nextButton);
        }

        deleteButton.setText("Xóa");
        deleteButton.setCallbackData("/baocao_delete");
        buttonsInline3.add(deleteButton);
        rowsInline.add(buttonsInline1);
        rowsInline.add(buttonsInline3);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        return inlineKeyboardMarkup;
    }

    @SneakyThrows
    private void editReport(PageDto<List<Transaction>> pageTransaction, Long chatId, Integer messageId, String... dateTime) {
        if (pageTransaction.getPageItems().isEmpty()) {
            deleteMessage(chatId, messageId);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId.toString());
            editMessageText.setMessageId(messageId);
            long sumIncome;
            long sumExpense;

            if (!dateTime[0].isEmpty()) {
                if (dateTime[0].split("/").length == 3){
                    sumIncome = transactionService.getSumAmountByUser_IdAndTransactionDateAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.formatDateString(dateTime[0], "dd/MM/yyyy", "yyyy-MM-dd"), TransactionType.INCOMING);
                    sumExpense = transactionService.getSumAmountByUser_IdAndTransactionDateAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.formatDateString(dateTime[0], "dd/MM/yyyy", "yyyy-MM-dd"), TransactionType.EXPENSE);
                } else {
                    sumIncome = transactionService.getSumAmountByUser_IdAndTransactionMonthAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.mmYyyyToYyyyMm(dateTime[0]), TransactionType.INCOMING);
                    sumExpense = transactionService.getSumAmountByUser_IdAndTransactionMonthAndType(pageTransaction.getPageItems().get(0).getUser().getId(), DateUtils.mmYyyyToYyyyMm(dateTime[0]), TransactionType.EXPENSE);
                }
            } else {
                sumIncome = transactionService.getSumAmountByUser_Id(pageTransaction.getPageItems().get(0).getUser().getId(), TransactionType.INCOMING);
                sumExpense = transactionService.getSumAmountByUser_Id(pageTransaction.getPageItems().get(0).getUser().getId(), TransactionType.EXPENSE);
            }

            editMessageText.setText(formatTransactions(pageTransaction, sumIncome, sumExpense));
            editMessageText.setParseMode("HTML");
            editMessageText.setReplyMarkup(getKeyboard(pageTransaction.getPageNo(), pageTransaction.getPageSize(), pageTransaction.getTotalPages(), dateTime));
            webhookBot.execute(editMessageText);
        }
    }

    @SneakyThrows
    private void deleteMessage(Long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        webhookBot.execute(deleteMessage);
    }

    private String formatTransactions(PageDto<List<Transaction>> pageTransactions, long sumIncome, long sumExpense) {
        List<Transaction> transactions = pageTransactions.getPageItems();
        String header = "\uD83D\uDD04  <b>Giao Dịch Của " + transactions.get(0).getUser().getUsername() + "</b>  \uD83D\uDD04\n<b>-------------------------------------------------</b>\n";
        String amountChange = String.format("%+,d", (sumIncome - sumExpense));
        String subHeader = "";
        if (pageTransactions.getPageNo() == 0) {
            subHeader = """
                    <b>Tổng Thu:</b> <code>+%,d</code> <b>VND</b>
                    <b>Tổng Chi:</b> <code>-%,d</code> <b>VND</b>
                    <b>Thay Đổi:</b> <code>%s</code> <b>VND</b>
                    <b>-------------------------------------------------</b>
                    """.formatted(sumIncome, sumExpense, amountChange);
        }
        String body = transactions.stream().map(transactionService::formatTransaction).collect(Collectors.joining("-------------------------------------------------\n"));
        return header + subHeader + body;
    }

    private PageDto<List<Transaction>> getPageTransaction(int pageNo, int pageSize, Long telegramUserId, String... dateTime) {
        Long userId = userService.getUserIdByTelegramId(telegramUserId);

        if (dateTime != null && dateTime.length > 0 && !dateTime[0].isBlank()) {
            String reportTimeString = dateTime[0];
            try {
                LocalDate reportDay = LocalDate.parse(reportTimeString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDateTime from = reportDay.atStartOfDay();
                LocalDateTime to = reportDay.atTime(23, 59, 59);

                return transactionService.getPage(pageNo, pageSize, userId, from, to);
            } catch (Exception e) {
                try {
                    YearMonth reportMonth = YearMonth.parse(reportTimeString, DateTimeFormatter.ofPattern("MM/yyyy"));
                    LocalDateTime from = reportMonth.atDay(1).atStartOfDay();
                    LocalDateTime to = reportMonth.atEndOfMonth().atTime(23, 59, 59);

                    return transactionService.getPage(pageNo, pageSize, userId, from, to);
                } catch (Exception e1) {
                    log.error("parse Date exception: {}", e1.getMessage());
                }
            }
        }
        return transactionService.getPage(pageNo, pageSize, userId);
    }
}