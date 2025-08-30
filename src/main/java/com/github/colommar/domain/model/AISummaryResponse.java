package com.github.colommar.domain.model;

import lombok.Data;

/**
 * AI摘要响应模型
 */
@Data
public class AISummaryResponse {
    private int code;
    private String message;
    private int ttl;
    private ResponseData data;
    
    @Data
    public static class ResponseData {
        private int code;
        private ModelResult model_result;
        private String stid;
        private int status;
        private int like_num;
        private int dislike_num;
    }
    
    @Data
    public static class ModelResult {
        private int result_type;
        private String summary;
        private Outline[] outline;
        private Subtitle[] subtitle;
    }
    
    @Data
    public static class Outline {
        private String title;
        private PartOutline[] part_outline;
        private int timestamp;
    }
    
    @Data
    public static class PartOutline {
        private int timestamp;
        private String content;
    }
    
    @Data
    public static class Subtitle {
        private String title;
        private PartSubtitle[] part_subtitle;
        private int timestamp;
    }
    
    @Data
    public static class PartSubtitle {
        private int start_timestamp;
        private int end_timestamp;
        private String content;
    }
}
