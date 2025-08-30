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
        for(String arg : args) {
            if(arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
                printHelp();
                System.exit(0);
            }
        }

        try {
            printBanner();
            log.info("Starting Bilibili AI Subtitles application...");
            
            // 检查是否通过命令行参数运行
            boolean useArgs = args.length >= 2;
            
            String sessdata;
            String videoId;
            boolean isTimeDetailOn;
            boolean isFileOutput;
            
            if (useArgs) {
                // 解析命令行参数
                sessdata = args[0].trim();
                videoId = args[1].trim();
                
                if (args.length == 2) {
                    isFileOutput = true;
                    isTimeDetailOn = false;
                } else if (args.length == 3) {
                    isFileOutput = Boolean.parseBoolean(args[2]);
                    isTimeDetailOn = false;
                } else {
                    isFileOutput = Boolean.parseBoolean(args[2]);
                    isTimeDetailOn = Boolean.parseBoolean(args[3]);
                }
                
                // 设置命令行参数值到ConfigLoader
                ConfigLoader.setArgsValues(sessdata, videoId, isTimeDetailOn, isFileOutput);
            } else {
                // 从配置文件加载
                sessdata = ConfigLoader.loadSessdataFromConfig();
                videoId = ConfigLoader.loadVideoIdFromConfig();
                isTimeDetailOn = ConfigLoader.loadIsTimeDetailOnFromConfig();
                isFileOutput = ConfigLoader.loadIsFileOutputFromConfig();
            }
            
            log.info("Configuration loaded successfully - SESSDATA: {}, VideoId: {}, Time Detail: {}, File Output: {}", 
                    sessdata.substring(0, Math.min(10, sessdata.length())) + "...", 
                    videoId, isTimeDetailOn, isFileOutput);
            
            BiliWebService biliWebService = new BiliWebServiceImpl();
            
            AISummaryResponse response = biliWebService.getAISummary(videoId);

            if (response.getData() != null && response.getData().getModel_result() != null) {
                log.info("AI summary retrieved successfully");
                
                outputToConsole(response, isTimeDetailOn);
                
                if (isFileOutput) {
                    outputToFile(response, videoId, isTimeDetailOn);
                }
            } else {
                log.warn("No summary data retrieved");
            }
            
            log.info("Application execution completed");
            
        } catch (Exception e) {
            log.error("Application execution failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void printBanner() {
        System.out.println("             ,--.                                           \n" +
                " ,---. ,---. |  | ,---. ,--,--,--.,--,--,--. ,--,--.,--.--. \n" +
                "| .--'| .-. ||  || .-. ||        ||        |' ,-.  ||  .--' \n" +
                "\\ `--.' '-' '|  |' '-' '|  |  |  ||  |  |  |\\ '-'  ||  |    \n" +
                " `---' `---' `--' `---' `--`--`--'`--`--`--' `--`--'`--'    \n" +
                "                                                           ");
    }

    // 显示帮助函数
    public static void printHelp() {
        printBanner();
        System.out.println("Usage:");
        System.out.println("  myapp.exe <sessdata> <videoId> [isTimeDetailOn] [isFileOutput]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  sessdata        (required) Your session data");
        System.out.println("  videoId         (required) Video ID to process");
        System.out.println("  isFileOutput    (optional) true/false, default from config(true)");
        System.out.println("  isTimeDetailOn  (optional) true/false, default from config(false)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  myapp.exe abc123 xyz789");
        System.out.println("      -> uses default isFileOutput and isTimeDetailOn from config");
        System.out.println("  myapp.exe abc123 xyz789 true");
        System.out.println("      -> overrides isFileOutput, uses default isTimeDetailOn");
        System.out.println("  myapp.exe abc123 xyz789 false true");
        System.out.println("      -> overrides both optional parameters");
        System.out.println("  myapp.exe -h");
        System.out.println("      -> shows this help message");
        System.out.println();
        System.out.println("Project URL: https://github.com/colommar/bilibiliAISubtitles");
    }

    /**
     * 输出到控制台
     */
    private static void outputToConsole(AISummaryResponse response, boolean isTimeDetailOn) {
        System.out.println("AI summary retrieved successfully");
        System.out.println("Summary: " + response.getData().getModel_result().getSummary());
        
        // Output outline
        System.out.println("\n=== Video Outline ===");
        for (AISummaryResponse.Outline outline : response.getData().getModel_result().getOutline()) {
            if (isTimeDetailOn) {
                System.out.println("Title: " + outline.getTitle() + " (Timestamp: " + outline.getTimestamp() + "s)");
            } else {
                System.out.println("Title: " + outline.getTitle());
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