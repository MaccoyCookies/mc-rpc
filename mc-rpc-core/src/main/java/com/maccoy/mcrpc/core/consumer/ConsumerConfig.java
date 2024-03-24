package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.cluster.RandomLoadBalancer;
import com.maccoy.mcrpc.core.filter.CacheFilter;
import com.maccoy.mcrpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;

/**
 * @author Maccoy
 * @date 2024/3/10 19:49
 * Description
 */
@Configuration
public class ConsumerConfig {

    @Value("${mcrpc.providers}")
    String servers;

    @Bean
    public ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerRunner(ConsumerBootstrap consumerBootstrap) {
        return x -> {
            consumerBootstrap.start();
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return new RandomLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean
    public Filter filter() {
        return new CacheFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
        return new ZkRegistryCenter();
    }

}
