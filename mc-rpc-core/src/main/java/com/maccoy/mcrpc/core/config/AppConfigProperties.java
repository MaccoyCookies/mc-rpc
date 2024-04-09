package com.maccoy.mcrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mcrpc.app")
public class AppConfigProperties {

    private String id;

    private String namespace;

    private String env;

}
