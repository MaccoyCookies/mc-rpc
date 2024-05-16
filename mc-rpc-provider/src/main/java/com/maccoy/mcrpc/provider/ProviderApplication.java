package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.config.ProviderConfig;
import io.github.maccoycookies.mcconfig.client.annotation.EnableMcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

@Import({ProviderConfig.class})
@RestController
@SpringBootApplication
@EnableMcConfig
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}
