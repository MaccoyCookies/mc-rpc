package com.maccoy.mcrpc.core.cluster;

import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.meta.InstanceMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maccoy
 * @date 2024/3/17 18:54
 * Description
 */
public class RoundLoadBalancer implements LoadBalancer {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        return providers.get((atomicInteger.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
