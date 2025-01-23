package pro.vhbchieu.telegram_tc_bot.config.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GeminiBodyDto {
    private List<Candidate> candidates;
    private UsageMetadata usageMetaData;
    private String modelVersion;

    @Getter
    @AllArgsConstructor
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Double avgLogprobs;

        @Getter
        @AllArgsConstructor
        public static class Content {
            private List<Part> parts;
            private String role;

            @Getter
            @AllArgsConstructor
            public static class Part {
                private String text;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;
    }
}
