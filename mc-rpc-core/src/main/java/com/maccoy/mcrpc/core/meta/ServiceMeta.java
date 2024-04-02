package com.maccoy.mcrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/22 22:47
 * Description 描述服务元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta {

    private String app;

    private String namespace;

    private String env;

    private String name;

    private Map<String, String> parameters = new HashMap<>();

    public ServiceMeta(String app, String namespace, String env, String name) {
        this.app = app;
        this.namespace = namespace;
        this.env = env;
        this.name = name;
    }

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }

    public String toMetas() {
        return JSON.toJSONString(parameters);
    }

}
