package com.github.colommar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WBI密钥模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WbiKeys {
    private String imgKey;
    private String subKey;
}
