package com.maccoy.mcrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/22 22:28
 * Description 描述服务实例元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceMeta {

    private String schema;

    private String host;

    private Integer port;

    private String context;

    /**
     * online of offline
     */
    private boolean status;

    private Map<String, String> parameters;

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", schema, host, port, context);
    }
}
