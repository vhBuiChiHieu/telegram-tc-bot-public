package pro.vhbchieu.telegram_tc_bot.sys.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.vhbchieu.telegram_tc_bot.config.common.SpeechToTextDto;
import pro.vhbchieu.telegram_tc_bot.sys.service.VoiceService;
import pro.vhbchieu.telegram_tc_bot.utils.TelegramUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceServiceImpl implements VoiceService {

    @Value("${python.stt.endpoint.url}")
    private String sttEndpointUrl;

    private static final int MAX_VOICE_SIZE = 100;  //kb
    private static final String FILE_EXTENSION = ".ogg";

    private final TelegramWebhookBot webhookBot;
    private final RestTemplate restTemplate;

    /**
     * Trans voice message to text
     *
     * @param update update of voice message
     */
    @SneakyThrows
    @Override
    public void speechToTextHandle(Update update) {
        if (!update.getMessage().hasVoice() || (update.getMessage().getVoice().getFileSize() > MAX_VOICE_SIZE * 1024))
            return;

        TelegramUtils.sendMessage(update.getMessage().getChatId().toString(), "Bạn vừa gửi tin nhắn thoại, vui lòng đợi...");
        GetFile getFile = new GetFile(update.getMessage().getVoice().getFileId());   //Telegram bot method
        File file = webhookBot.execute(getFile);    //TelegramApiException
        try (InputStream fileStream = webhookBot.downloadFileAsStream(file)) {
            //Send request voice to text
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file", new InputStreamResource(fileStream) {
                @Override
                public String getFilename() {
                    return UUID.randomUUID() + FILE_EXTENSION;
                }

                @Override
                public long contentLength() throws IOException {
                    return -1;
                }
            });
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            SpeechToTextDto sttDto = restTemplate.postForObject(sttEndpointUrl, request, SpeechToTextDto.class); //RestClientException
            update.getMessage().setText(sttDto.getTranscription().trim());
        }
    }
}
