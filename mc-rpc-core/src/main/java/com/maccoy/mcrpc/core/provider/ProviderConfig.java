package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.registry.ZkRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean(initMethod = "start", destroyMethod = "stop")
    RegisterCenter registerCenter() {
        return new ZkRegistryCenter();
    }

}
