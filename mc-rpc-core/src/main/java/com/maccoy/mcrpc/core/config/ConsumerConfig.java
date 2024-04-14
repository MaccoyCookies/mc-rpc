package com.maccoy.mcrpc.core.config;

import com.maccoy.mcrpc.core.api.Filter;
import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.api.Router;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.core.cluster.GrayRouter;
import com.maccoy.mcrpc.core.cluster.RandomLoadBalancer;
import com.maccoy.mcrpc.core.consumer.ConsumerBootstrap;
import com.maccoy.mcrpc.core.filter.ParamFilter;
import com.maccoy.mcrpc.core.registry.zk.ZkRegistryCenter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/10 19:49
 * Description
 */
@Data
@Configuration
@Import({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    @Autowired
    AppProperties appProperties;

    @Autowired
    ConsumerProperties consumerProperties;

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
        return new GrayRouter(consumerProperties.getGrayRatio());
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

    @Bean
    RpcContext rpcContext(@Autowired Router router,
                          @Autowired LoadBalancer loadBalancer,
                          @Autowired List<Filter> filters) {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadBalancer(loadBalancer);
        rpcContext.setFilters(filters);
        rpcContext.getParameters().put("app.id", appProperties.getId());
        rpcContext.getParameters().put("app.namespace", appProperties.getNamespace());
        rpcContext.getParameters().put("app.env", appProperties.getEnv());
        rpcContext.getParameters().put("consumer.retries", String.valueOf(consumerProperties.getRetries()));
        rpcContext.getParameters().put("consumer.timeout", String.valueOf(consumerProperties.getTimeout()));
        rpcContext.getParameters().put("consumer.faultLimit", String.valueOf(consumerProperties.getFaultLimit()));
        rpcContext.getParameters().put("consumer.halfOpenInitialDelay", String.valueOf(consumerProperties.getHalfOpenInitialDelay()));
        rpcContext.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerProperties.getHalfOpenDelay()));
        rpcContext.setConsumerProperties(consumerProperties);
        return rpcContext;
    }

    @Bean
    @ConditionalOnMissingBean
    ApolloChangedListener consumer_apolloChangedListener() {
        return new ApolloChangedListener();
    }

}
