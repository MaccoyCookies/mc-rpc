package com.maccoy.mcrpc.core.config;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.provider.ProviderBootstrap;
import com.maccoy.mcrpc.core.provider.ProviderInvoker;
import com.maccoy.mcrpc.core.registry.zk.ZkRegistryCenter;
import com.maccoy.mcrpc.core.transport.SpringBootTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @author Maccoy
 * @date 2024/3/10 10:57
 * Description
 */
@Configuration
@Import({AppConfigProperties.class, ProviderConfigProperties.class, SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port}")
    private Integer port;

    @Autowired
    private AppConfigProperties appConfigProperties;

    @Autowired
    private ProviderConfigProperties providerConfigProperties;

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap(port, appConfigProperties, providerConfigProperties);
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
