package com.maccoy.mcrpc.core.registry.zk;

import com.alibaba.fastjson.JSON;
import com.maccoy.mcrpc.core.api.RpcException;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;
import com.maccoy.mcrpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/3/17 20:24
 * Description
 */
@Slf4j
public class ZkRegistryCenter implements RegisterCenter {

    @Value("${mcrpc.zkServer:localhost:2181}")
    private String zkServer;

    @Value("${mcrpc.zkRoot:mcrpc}")
    private String zkRoot;

    private CuratorFramework curatorFramework = null;

    private boolean running = false;

    @Override
    public synchronized void start() {
        if (running) {
            log.info(" ===> zk client has started to server[" + zkServer + "/" + zkRoot + "], ignored.");
            return;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkServer).namespace(zkRoot).retryPolicy(retryPolicy).build();
        log.info("zk client start to server[" + zkServer +"] ...");
        curatorFramework.start();
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            log.info(" ===> zk client isn't running to server[" + zkServer + "/" + zkRoot + "], ignored.");
            return;
        }
        log.info("zk stop ...");
        curatorFramework.close();
    }

    @Override
    public void register(ServiceMeta serviceMeta, InstanceMeta instance) {
        String servicePath = "/" + serviceMeta.toPath();
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, serviceMeta.toMetas().getBytes());
            }
            String instancePath = servicePath + "/" + instance.toPath();
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas().getBytes());
            log.info("zk register ... " + servicePath);
        } catch (Exception exception) {
            throw new RpcException(exception);
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
            log.info("zk unregister ..." + servicePath);
        } catch (Exception exception) {
            throw new RpcException(exception);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta serviceMeta) {
        String servicePath = "/" + serviceMeta.toPath();
        try {
            List<String> nodes = curatorFramework.getChildren().forPath(servicePath);
            log.info("fetchAll from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstances(servicePath, nodes);
        } catch (Exception exception) {
            throw new RpcException(exception);
        }
    }

    private List<InstanceMeta> mapInstances(String servicePath, List<String> nodes) {
        return nodes.stream().map(node -> {
            String[] split = node.split("_");
            InstanceMeta instanceMeta = InstanceMeta.http(split[0], Integer.valueOf(split[1]));
            System.out.println(" instance: " + instanceMeta.toUrl());
            String nodePath = servicePath + "/" + node;
            try {
                String data = new String(curatorFramework.getData().forPath(nodePath));
                HashMap params = JSON.parseObject(data, HashMap.class);
                params.entrySet().stream().forEach(System.out::println);
                instanceMeta.setParameters(params);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return instanceMeta;
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta serviceMeta, ChangedListener listener) {
        final TreeCache treeCache = TreeCache.newBuilder(curatorFramework, "/" + serviceMeta.toPath()).setCacheData(true).setMaxDepth(2).build();

        treeCache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动
            log.info("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(serviceMeta);
            listener.fire(new Event(nodes));
        });
        treeCache.start();
    }

}
