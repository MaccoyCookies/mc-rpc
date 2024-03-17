package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.cluster.RandomLoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maccoy
 * @date 2024/3/17 18:06
 * Description
 */
public interface LoadBalancer<T> {

    T choose(List<T> providers);

    LoadBalancer Default = new RandomLoadBalancer();
}
