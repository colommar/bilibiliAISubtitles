package com.github.colommar.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP工具类
 */
@Slf4j
public class HttpUtil {
    
    private final OkHttpClient client;
    
    public HttpUtil() {
        this.client = new OkHttpClient();
    }
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException 请求失败时抛出异常
     */
    public String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP request failed: " + response);
            }
            
            String body = response.body().string();
            log.debug("HTTP GET {} - Status: {}", url, response.code());
            return body;
        }
    }
}
