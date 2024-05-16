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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maccoy
 * @date 2024/4/27 19:00
 * Description
 */
@Slf4j
public class McRegistryCenter implements RegisterCenter {

    private static final String REG_PATH = "/reg";
    private static final String UNREG_PATH = "/unreg";
    private static final String FETCH_ALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${mcregistry.servers}")
    private String servers;

    Map<String, Long> VERSIONS = new HashMap<>();

    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    McHealthChecker healthChecker = new McHealthChecker();

    @Override
    public void start() {
        log.info(" ===> [McRegistry] : start with server : {}", servers);
        healthChecker.start();
        providerCheck();
    }

    private void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().forEach((instance) -> {
                Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), renewsPath(RENEWS.get(instance)), Long.class);
                log.info(" ===> [McRegistry] : renew instance {} at {}", instance, timestamp);
            });
        });
    }

    @Override
    public void stop() {
        log.info(" ===> [McRegistry] : stop with server : {}", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===> [McRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info(" ===> [McRegistry] : registered instance {}", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ===> [McRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unregPath(service), InstanceMeta.class);
        log.info(" ===> [McRegistry] : unregistered instance {}", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ===> [McRegistry] : find all instances {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(fetchAllPath(service), new TypeReference<List<InstanceMeta>>() {});
        log.info(" ===> [McRegistry] : find all = {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        consumerCheck(service, listener);
    }

    private void consumerCheck(ServiceMeta service, ChangedListener listener) {
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ===> [McRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (version < newVersion) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }
    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }
    private String fetchAllPath(ServiceMeta service) {
        return path(FETCH_ALL_PATH, service);
    }
    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }
    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        String services = serviceList.stream().map(ServiceMeta::toPath).collect(Collectors.joining(","));
        log.info(" ====>>>> [McRegistry] : renew instance for {}", services);
        return servers + RENEWS_PATH + "?services=" + services;
    }

}
