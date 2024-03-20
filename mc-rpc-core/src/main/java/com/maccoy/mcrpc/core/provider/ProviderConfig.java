package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.registry.ZkRegistryCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author Maccoy
 * @date 2024/3/10 10:57
 * Description
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerRunner(ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    RegisterCenter registerCenter() {
        return new ZkRegistryCenter();
    }

}
