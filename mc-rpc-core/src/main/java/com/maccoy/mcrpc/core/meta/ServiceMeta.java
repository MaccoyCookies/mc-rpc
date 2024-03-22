package com.maccoy.mcrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }


}
