package com.maccoy.mcrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mcrpc.app")
public class AppProperties {

    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";

}
