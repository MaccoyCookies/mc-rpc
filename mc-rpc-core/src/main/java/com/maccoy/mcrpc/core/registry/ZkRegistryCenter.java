package com.maccoy.mcrpc.core.registry;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println("zk start ...");
        curatorFramework.start();
    }

    @Override
    public void stop() {
        System.out.println("zk stop ...");
        curatorFramework.close();
    }

    @Override
    public void register(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            String instancePath = servicePath + "/" + instance.toPath();
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            System.out.println("zk register ... " + servicePath);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregister(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                return;
            }
            String instancePath = servicePath + "/" + instance.toPath();
            curatorFramework.delete().quietly().forPath(instancePath);
            System.out.println("zk unregister ..." + servicePath);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(String serviceName) {
        String servicePath = "/" + serviceName;
        try {
            List<String> nodes = curatorFramework.getChildren().forPath(servicePath);
            System.out.println("fetchAll from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstances(nodes);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static List<InstanceMeta> mapInstances(List<String> nodes) {
        return nodes.stream().map(node -> {
            String[] split = node.split("_");
            return InstanceMeta.http(split[0], Integer.valueOf(split[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, ChangedListener listener) {
        final TreeCache treeCache = TreeCache.newBuilder(curatorFramework, "/" + service).setCacheData(true).setMaxDepth(2).build();

        treeCache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动
            System.out.println("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        treeCache.start();
    }
}
