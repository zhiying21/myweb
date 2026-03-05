package com.zhiying.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {
    /**
     * 上传文件存储路径
     */
    private String path = "./uploads";
}
