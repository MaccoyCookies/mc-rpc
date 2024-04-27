package com.maccoy.mcrpc.core.registry.mc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.maccoy.mcrpc.core.api.RegisterCenter;
import com.maccoy.mcrpc.core.consumer.HttpInvoker;
import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;
import com.maccoy.mcrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Maccoy
 * @date 2024/4/27 19:00
 * Description
 */
@Slf4j
public class McRegistryCenter implements RegisterCenter {

    @Value("${mcregistry.servers}")
    private String servers;

    @Override
    public void start() {
        log.info(" ===> [McRegistry] : start with server : {}", servers);
        executorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" ===> [McRegistry] : stop with server : {}", servers);
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===> [McRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/reg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ===> [McRegistry] : registered instance {}", instance);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===> [McRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ===> [McRegistry] : unregistered instance {}", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ===> [McRegistry] : find all instances {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<List<InstanceMeta>>() {});
        log.info(" ===> [McRegistry] : find all = {}", instances);
        return instances;
    }

    Map<String, Long> VERSIONS = new HashMap<>();

    ScheduledExecutorService executorService;

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        executorService.scheduleWithFixedDelay(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service = " + service.toPath(), Long.class);
            log.info(" ===> [McRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (version < newVersion) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }

        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
