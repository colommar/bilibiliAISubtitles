package com.github.colommar.infrastructure.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * WBI工具类
 */
@Slf4j
public class WbiUtil {
    
    private static final int[] MIXIN_KEY_ENC_TAB = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };
    
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    
    /**
     * 生成WBI签名
     * 
     * @param params 参数
     * @param imgKey 图片密钥
     * @param subKey 子密钥
     * @return 编码后的查询字符串
     */
    public static String encodeWbi(Map<String, Object> params, String imgKey, String subKey) {
        String mixinKey = getMixinKey(imgKey, subKey);
        long currTime = System.currentTimeMillis() / 1000;
        params.put("wts", currTime);
        
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);
        String query = sortedParams.entrySet().stream()
                .map(entry -> {
                    String value = String.valueOf(entry.getValue()).replaceAll("[!'()*]", "");
                    try {
                        return encodeURIComponent(entry.getKey()) + "=" + encodeURIComponent(value);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("&"));
        
        String wbiSign = md5(query + mixinKey);
        return query + "&w_rid=" + wbiSign;
    }
    
    /**
     * 生成混合密钥
     */
    private static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            key.append(s.charAt(MIXIN_KEY_ENC_TAB[i]));
        }
        return key.toString();
    }
    
    /**
     * 计算MD5哈希
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] result = new char[messageDigest.length * 2];
            for (int i = 0; i < messageDigest.length; i++) {
                result[i * 2] = HEX_DIGITS[(messageDigest[i] >> 4) & 0xF];
                result[i * 2 + 1] = HEX_DIGITS[messageDigest[i] & 0xF];
            }
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }
    
    /**
     * URL编码
     */
    private static String encodeURIComponent(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, String.valueOf(StandardCharsets.UTF_8)).replace("+", "%20");
    }
}
