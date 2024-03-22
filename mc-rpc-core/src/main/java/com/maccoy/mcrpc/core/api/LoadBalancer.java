package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.cluster.RandomLoadBalancer;
import com.maccoy.mcrpc.core.meta.InstanceMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maccoy
 * @date 2024/3/17 18:06
 * Description
 */
public interface LoadBalancer {

    InstanceMeta choose(List<InstanceMeta> providers);

    LoadBalancer Default = new RandomLoadBalancer();
}
