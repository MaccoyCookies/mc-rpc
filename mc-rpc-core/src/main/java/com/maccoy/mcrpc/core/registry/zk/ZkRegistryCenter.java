package com.maccoy.mcrpc.core.registry.zk;

import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;
import com.maccoy.mcrpc.core.registry.Event;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/3/17 20:24
 * Description
 */
public class ZkRegistryCenter implements RegisterCenter {

    @Value("${mcrpc.zkServer}")
    private String zkServer;

    @Value("${mcrpc.zkRoot}")
    private String zkRoot;

    private CuratorFramework curatorFramework = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkServer).namespace(zkRoot).retryPolicy(retryPolicy).build();
        System.out.println("zk client start to server[" + zkServer +"]...");
        curatorFramework.start();
    }

    @Override
    public void stop() {
        System.out.println("zk stop ...");
        curatorFramework.close();
    }

    @Override
    public void register(ServiceMeta serviceMeta, InstanceMeta instance) {
        String servicePath = "/" + serviceMeta.toPath();
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
    public void unregister(ServiceMeta serviceMeta, InstanceMeta instance) {
        String servicePath = "/" + serviceMeta.toPath();
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
    public List<InstanceMeta> fetchAll(ServiceMeta serviceMeta) {
        String servicePath = "/" + serviceMeta.toPath();
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
    public void subscribe(ServiceMeta serviceMeta, ChangedListener listener) {
        final TreeCache treeCache = TreeCache.newBuilder(curatorFramework, "/" + serviceMeta.toPath()).setCacheData(true).setMaxDepth(2).build();

        treeCache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动
            System.out.println("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(serviceMeta);
            listener.fire(new Event(nodes));
        });
        treeCache.start();
    }
}
