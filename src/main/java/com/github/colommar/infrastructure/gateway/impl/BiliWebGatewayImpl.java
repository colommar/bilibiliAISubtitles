package com.github.colommar.infrastructure.gateway.impl;

import com.github.colommar.domain.model.AISummaryResponse;
import com.github.colommar.domain.model.VideoDetails;
import com.github.colommar.domain.model.WbiKeys;
import com.github.colommar.infrastructure.gateway.BiliWebGateway;
import com.github.colommar.infrastructure.util.HttpUtil;
import com.github.colommar.infrastructure.util.WbiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * B站Web网关实现类
 */
@Slf4j
public class BiliWebGatewayImpl implements BiliWebGateway {
    
    private final HttpUtil httpUtil;
    private final Gson gson;
    private final String sessdata;
    
    public BiliWebGatewayImpl() {
        this.httpUtil = new HttpUtil();
        this.gson = new Gson();
        try {
            this.sessdata = com.github.colommar.infrastructure.config.ConfigLoader.loadSessdataFromConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SESSDATA", e);
        }
    }
    
    @Override
    public VideoDetails getVideoDetails(String bvid) throws Exception {
        String url = "https://api.bilibili.com/x/web-interface/view?bvid=" + encodeURIComponent(bvid);
        
        String response = httpUtil.get(url, createHeaders());
        log.debug("Video details response: {}", response);
        
        JsonObject json = gson.fromJson(response, JsonObject.class);
        if (json.get("code").getAsInt() != 0) {
            throw new Exception("API error in video details: " + response);
        }
        
        JsonObject data = json.getAsJsonObject("data");
        long cid = data.getAsJsonArray("pages").get(0).getAsJsonObject().get("cid").getAsLong();
        long upMid = data.getAsJsonObject("owner").get("mid").getAsLong();
        
        return new VideoDetails(cid, upMid);
    }
    
    @Override
    public WbiKeys getWbiKeys() throws Exception {
        String url = "https://api.bilibili.com/x/web-interface/nav";
        
        String response = httpUtil.get(url, createHeaders());
        JsonObject json = gson.fromJson(response, JsonObject.class);
        
        if (json.get("code").getAsInt() == -101) {
            throw new Exception("Failed to get WBI keys: Account not logged in");
        }
        
        JsonObject wbiImg = json.getAsJsonObject("data").getAsJsonObject("wbi_img");
        String imgUrl = wbiImg.get("img_url").getAsString();
        String subUrl = wbiImg.get("sub_url").getAsString();
        
        return new WbiKeys(
                imgUrl.substring(imgUrl.lastIndexOf('/') + 1, imgUrl.lastIndexOf('.')),
                subUrl.substring(subUrl.lastIndexOf('/') + 1, subUrl.lastIndexOf('.'))
        );
    }
    
    @Override
    public AISummaryResponse getAISummary(String bvid, VideoDetails videoDetails, WbiKeys wbiKeys) throws Exception {
        Map<String, Object> params = new TreeMap<>();
        params.put("bvid", bvid);
        params.put("cid", videoDetails.getCid());
        params.put("up_mid", videoDetails.getUpMid());
        
        String query = WbiUtil.encodeWbi(params, wbiKeys.getImgKey(), wbiKeys.getSubKey());
        String url = "https://api.bilibili.com/x/web-interface/view/conclusion/get?" + query;
        
        String response = httpUtil.get(url, createHeaders());
        log.debug("AI summary response: {}", response);
        
        JsonObject json = gson.fromJson(response, JsonObject.class);
        if (json.get("code").getAsInt() != 0) {
            throw new Exception("API error: " + response);
        }
        
        return gson.fromJson(response, AISummaryResponse.class);
    }
    
    /**
     * 创建HTTP请求头
     */
    private Map<String, String> createHeaders() {
        Map<String, String> headers = new TreeMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Referer", "https://www.bilibili.com/");
        headers.put("Cookie", "SESSDATA=" + sessdata);
        return headers;
    }
    
    /**
     * URL编码
     */
    private String encodeURIComponent(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, String.valueOf(StandardCharsets.UTF_8)).replace("+", "%20");
    }
}
