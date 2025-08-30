package com.github.colommar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频详情模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetails {
    private long cid;
    private long upMid;
}
