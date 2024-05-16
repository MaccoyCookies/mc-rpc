package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.config.ApolloChangedListener;
import com.maccoy.mcrpc.core.config.ProviderConfig;
import io.github.maccoycookies.mcconfig.client.annotation.EnableMcConfig;
import com.maccoy.mcrpc.core.config.ProviderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Import({ProviderConfig.class})
@RestController
@SpringBootApplication
@EnableMcConfig
public class ProviderApplication {

    // @Bean
    // ApolloChangedListener apolloChangedListener() {
    //     return new ApolloChangedListener();
    // }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ProviderApplication.class, args);
    }

    @Autowired
    ProviderProperties providerProperties;

    @GetMapping("/meta")
    public String meta() {
        return providerProperties.getMetas().toString();
    }

}
