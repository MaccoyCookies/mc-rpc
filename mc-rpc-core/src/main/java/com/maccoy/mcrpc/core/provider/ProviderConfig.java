package com.maccoy.mcrpc.core.provider;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    public ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
        };
    }

    @Bean
    public RegisterCenter registerCenter() {
        return new ZkRegistryCenter();
    }



}
