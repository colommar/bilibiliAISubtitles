package com.github.colommar.application.service.impl;

import com.github.colommar.application.service.BiliWebService;
import com.github.colommar.domain.model.AISummaryResponse;
import com.github.colommar.domain.service.BiliWebDomainService;
import com.github.colommar.domain.service.impl.BiliWebDomainServiceImpl;
import com.github.colommar.infrastructure.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

/**
 * B站AI字幕服务实现类
 */
@Slf4j
public class BiliWebServiceImpl implements BiliWebService {
    
    private final BiliWebDomainService biliWebDomainService;
    
    public BiliWebServiceImpl() {
        this.biliWebDomainService = new BiliWebDomainServiceImpl();
    }
    
    @Override
    public AISummaryResponse getAISummary(String bvid) throws Exception {
        try {
            // 参数校验
            Validate.notBlank(bvid, "视频BV号不能为空");
            
            log.info("开始获取视频AI摘要，BV号: {}", bvid);
            
            AISummaryResponse response = biliWebDomainService.getAISummary(bvid);
            
            log.info("成功获取视频AI摘要");
            return response;
            
        } catch (Exception e) {
            log.error("获取视频AI摘要失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
