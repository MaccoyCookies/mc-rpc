package com.maccoy.mcrpc.core.consumer;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.cluster.GrayRouter;
import com.maccoy.mcrpc.core.cluster.RandomLoadBalancer;
import com.maccoy.mcrpc.core.cluster.RoundLoadBalancer;
import com.maccoy.mcrpc.core.filter.CacheFilter;
import com.maccoy.mcrpc.core.filter.MockFilter;
import com.maccoy.mcrpc.core.filter.ParamFilter;
import com.maccoy.mcrpc.core.registry.zk.ZkRegistryCenter;
import lombok.Data;
import okhttp3.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/10 19:49
 * Description
 */
@Data
@Configuration
public class ConsumerConfig {

    @Value("${app.grayRatio}")
    private int grayRatio;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    @Value("${app.reties}")
    private Integer reties;

    @Value("${app.timeout}")
    private Integer timeout;

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
        return new GrayRouter(grayRatio);
    }

    @Bean
    public Filter filter() {
        // return new CacheFilter();
        return Filter.Default;
    }

    @Bean
    public Filter paramFilter() {
        return new ParamFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter registerCenter() {
        return new ZkRegistryCenter();
    }

}
