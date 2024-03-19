package com.maccoy.mcrpc.core.registry;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/17 20:24
 * Description
 */
public class ZkRegistryCenter implements RegisterCenter {

    private CuratorFramework curatorFramework = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder().connectString("localhost:2181").namespace("mcrpc").retryPolicy(retryPolicy).build();
    }

    @Override
    public void stop() {
        curatorFramework.close();
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            String instancePath = servicePath + "/" + instance;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                return;
            }
            String instancePath = servicePath + "/" + instance;
            curatorFramework.delete().quietly().forPath(instancePath);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<String> fetchAll(String serviceName) {
        return null;
    }
}
