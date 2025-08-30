package com.github.colommar.domain.service;

import com.github.colommar.domain.model.AISummaryResponse;

/**
 * B站AI字幕领域服务接口
 */
public interface BiliWebDomainService {
    
    /**
     * 获取视频AI摘要
     * 
     * @param bvid 视频BV号
     * @return AI摘要响应
     * @throws Exception 获取失败时抛出异常
     */
    AISummaryResponse getAISummary(String bvid) throws Exception;
}
