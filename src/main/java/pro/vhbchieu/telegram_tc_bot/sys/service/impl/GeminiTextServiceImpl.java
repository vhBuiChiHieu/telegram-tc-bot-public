package pro.vhbchieu.telegram_tc_bot.sys.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pro.vhbchieu.telegram_tc_bot.config.common.GeminiBodyDto;
import pro.vhbchieu.telegram_tc_bot.sys.service.TextService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiTextServiceImpl implements TextService {

    @Value("${ai.gemini.api.url}")
    String geminiApiUrl;

    private final RestTemplate restTemplate;

    /**
     * Use the Gemini API to convert user messages into command transaction
     * @param input user message text
     * @return {@code command} or {@code false} String of command
     */
    @Override
    public String convertToTransactionCommand(String input) {
        String jsonRequest = """
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "chỉ trong 1 câu, chuyển câu sau '%s' sang định dạng /thu hoặc chi số_tiền mô_tả , ví dụ /thu 50000 bán nhà, /chi 20000 mua cá. Nếu câu đó không liên quan đến thu chi tiền, hoặc không thể chuyển đúng dạng chuẩn hãy trả lời 'false'"
                          }
                        ]
                      }
                    ],
                    "generationConfig": {
                      "temperature": 0
                    }
                  }
                """.formatted(input);

        // set request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);
        //post
        ResponseEntity<GeminiBodyDto> responseEntity = restTemplate.postForEntity(geminiApiUrl, requestEntity, GeminiBodyDto.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            GeminiBodyDto body = responseEntity.getBody();
            return body.getCandidates().get(0).getContent().getParts().get(0).getText().trim();
        } else {
            log.error("Gemini convertToTransactionCommand: statusCode: {}", responseEntity.getStatusCode());
            return "false";
        }
    }
}
