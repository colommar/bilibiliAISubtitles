package com.github.colommar.infrastructure.gateway;

import com.github.colommar.domain.model.AISummaryResponse;
import com.github.colommar.domain.model.VideoDetails;
import com.github.colommar.domain.model.WbiKeys;

/**
 * B站Web网关接口
 */
public interface BiliWebGateway {
    
    /**
     * 获取视频详情
     * 
     * @param bvid 视频BV号
     * @return 视频详情
     * @throws Exception 获取失败时抛出异常
     */
    VideoDetails getVideoDetails(String bvid) throws Exception;
    
    /**
     * 获取WBI密钥
     * 
     * @return WBI密钥
     * @throws Exception 获取失败时抛出异常
     */
    WbiKeys getWbiKeys() throws Exception;
    
    /**
     * 获取AI摘要
     * 
     * @param bvid 视频BV号
     * @param videoDetails 视频详情
     * @param wbiKeys WBI密钥
     * @return AI摘要响应
     * @throws Exception 获取失败时抛出异常
     */
    AISummaryResponse getAISummary(String bvid, VideoDetails videoDetails, WbiKeys wbiKeys) throws Exception;
}
