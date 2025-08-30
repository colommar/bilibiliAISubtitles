package com.github.colommar.domain.service.impl;

import com.github.colommar.domain.model.AISummaryResponse;
import com.github.colommar.domain.model.VideoDetails;
import com.github.colommar.domain.model.WbiKeys;
import com.github.colommar.domain.service.BiliWebDomainService;
import com.github.colommar.infrastructure.gateway.BiliWebGateway;
import com.github.colommar.infrastructure.gateway.impl.BiliWebGatewayImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * B站AI字幕领域服务实现类
 */
@Slf4j
public class BiliWebDomainServiceImpl implements BiliWebDomainService {
    
    private final BiliWebGateway biliWebGateway;
    
    public BiliWebDomainServiceImpl() {
        this.biliWebGateway = new BiliWebGatewayImpl();
    }
    
    @Override
    public AISummaryResponse getAISummary(String bvid) throws Exception {
        log.info("Domain service starting to process AI summary request, BV ID: {}", bvid);
        
        VideoDetails videoDetails = biliWebGateway.getVideoDetails(bvid);
        log.debug("Successfully retrieved video details, CID: {}, UP ID: {}", videoDetails.getCid(), videoDetails.getUpMid());
        
        WbiKeys wbiKeys = biliWebGateway.getWbiKeys();
        log.debug("Successfully retrieved WBI keys");
        
        AISummaryResponse response = biliWebGateway.getAISummary(bvid, videoDetails, wbiKeys);
        log.info("Successfully retrieved AI summary");
        
        return response;
    }
}
