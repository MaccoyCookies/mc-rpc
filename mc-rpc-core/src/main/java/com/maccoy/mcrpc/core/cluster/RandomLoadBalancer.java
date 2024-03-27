package com.maccoy.mcrpc.core.cluster;

import com.maccoy.mcrpc.core.api.LoadBalancer;
import com.maccoy.mcrpc.core.meta.InstanceMeta;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Maccoy
 * @date 2024/3/17 18:53
 * Description
 */
public class RandomLoadBalancer implements LoadBalancer {

    Random random = new Random();

    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
