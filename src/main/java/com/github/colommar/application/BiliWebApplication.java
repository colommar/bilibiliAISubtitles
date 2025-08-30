package com.github.colommar.application;

import com.github.colommar.application.service.BiliWebService;
import com.github.colommar.application.service.impl.BiliWebServiceImpl;
import com.github.colommar.infrastructure.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;
import com.github.colommar.domain.model.AISummaryResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * B站AI字幕应用主类
 */
@Slf4j
public class BiliWebApplication {
    
    public static void main(String[] args) {
        try {
            log.info("启动B站AI字幕应用...");
            
            // 加载配置
            String sessdata = ConfigLoader.loadSessdataFromConfig();
            String videoId = ConfigLoader.loadVideoIdFromConfig();
            boolean isTimeDetailOn = ConfigLoader.loadIsTimeDetailOnFromConfig();
            boolean isFileOutput = ConfigLoader.loadIsFileOutputFromConfig();
            
            log.info("配置加载完成 - SESSDATA: {}, VideoId: {}, 时间详情: {}, 文件输出: {}", 
                    sessdata.substring(0, Math.min(10, sessdata.length())) + "...", 
                    videoId, isTimeDetailOn, isFileOutput);
            
            BiliWebService biliWebService = new BiliWebServiceImpl();
            
            AISummaryResponse response = biliWebService.getAISummary(videoId);

            if (response.getData() != null && response.getData().getModel_result() != null) {
                log.info("获取AI摘要成功");
                
                outputToConsole(response, isTimeDetailOn);
                
                if (isFileOutput) {
                    outputToFile(response, videoId, isTimeDetailOn);
                }
            } else {
                log.warn("未获取到摘要数据");
            }
            
            log.info("应用执行完成");
            
        } catch (Exception e) {
            log.error("应用执行失败: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * 输出到控制台
     */
    private static void outputToConsole(AISummaryResponse response, boolean isTimeDetailOn) {
        System.out.println("获取AI摘要成功");
        System.out.println("摘要: " + response.getData().getModel_result().getSummary());
        
        // 输出大纲
        System.out.println("\n=== 视频大纲 ===");
        for (AISummaryResponse.Outline outline : response.getData().getModel_result().getOutline()) {
            if (isTimeDetailOn) {
                System.out.println("标题: " + outline.getTitle() + " (时间戳: " + outline.getTimestamp() + "s)");
            } else {
                System.out.println("标题: " + outline.getTitle());
            }
            for (AISummaryResponse.PartOutline part : outline.getPart_outline()) {
                if (isTimeDetailOn) {
                    System.out.println("  - " + part.getContent() + " (时间戳: " + part.getTimestamp() + "s)");
                } else {
                    System.out.println("  - " + part.getContent());
                }
            }
        }
        
        // 输出字幕
        System.out.println("\n=== AI字幕 ===");
        for (AISummaryResponse.Subtitle subtitle : response.getData().getModel_result().getSubtitle()) {
            String title = subtitle.getTitle().isEmpty() ? "无标题" : subtitle.getTitle();
            if (isTimeDetailOn) {
                System.out.println("标题: " + title + " (时间戳: " + subtitle.getTimestamp() + "s)");
            } else {
                System.out.println("标题: " + title);
            }
            for (AISummaryResponse.PartSubtitle part : subtitle.getPart_subtitle()) {
                if (isTimeDetailOn) {
                    System.out.println("  - " + part.getContent() + " (" + part.getStart_timestamp() + "s - " + part.getEnd_timestamp() + "s)");
                } else {
                    System.out.println("  - " + part.getContent());
                }
            }
        }
    }
    
    /**
     * 输出到文件
     */
    private static void outputToFile(AISummaryResponse response, String videoId, boolean isTimeDetailOn) {
        try {
            // 生成文件名：VideoId + 时间戳
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = videoId + "_" + timestamp + ".txt";
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
                writer.println("B站AI字幕摘要 - " + videoId);
                writer.println("生成时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                writer.println("==================================================");
                
                writer.println("摘要: " + response.getData().getModel_result().getSummary());
                
                // 输出大纲
                writer.println("\n=== 视频大纲 ===");
                for (AISummaryResponse.Outline outline : response.getData().getModel_result().getOutline()) {
                    if (isTimeDetailOn) {
                        writer.println("标题: " + outline.getTitle() + " (时间戳: " + outline.getTimestamp() + "s)");
                    } else {
                        writer.println("标题: " + outline.getTitle());
                    }
                    for (AISummaryResponse.PartOutline part : outline.getPart_outline()) {
                        if (isTimeDetailOn) {
                            writer.println("  - " + part.getContent() + " (时间戳: " + part.getTimestamp() + "s)");
                        } else {
                            writer.println("  - " + part.getContent());
                        }
                    }
                }
                
                // 输出字幕
                writer.println("\n=== AI字幕 ===");
                for (AISummaryResponse.Subtitle subtitle : response.getData().getModel_result().getSubtitle()) {
                    String title = subtitle.getTitle().isEmpty() ? "无标题" : subtitle.getTitle();
                    if (isTimeDetailOn) {
                        writer.println("标题: " + title + " (时间戳: " + subtitle.getTimestamp() + "s)");
                    } else {
                        writer.println("标题: " + title);
                    }
                    for (AISummaryResponse.PartSubtitle part : subtitle.getPart_subtitle()) {
                        if (isTimeDetailOn) {
                            writer.println("  - " + part.getContent() + " (" + part.getStart_timestamp() + "s - " + part.getEnd_timestamp() + "s)");
                        } else {
                            writer.println("  - " + part.getContent());
                        }
                    }
                }
            }
            
            System.out.println("文件已保存: " + fileName);
            
        } catch (IOException e) {
            System.err.println("保存文件失败: " + e.getMessage());
        }
    }
}