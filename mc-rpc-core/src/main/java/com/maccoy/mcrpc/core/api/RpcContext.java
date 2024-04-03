package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maccoy
 * @date 2024/3/17 19:50
 * Description
 */
@Data
public class RpcContext {

    List<Filter> filters;

    Router<InstanceMeta> router;

    LoadBalancer loadBalancer;

    private Map<String, String> parameters = new HashMap<>();

    private static ThreadLocal<Map<String, String>> ContextParameters = ThreadLocal.withInitial(HashMap::new);

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }

    public static void clearContextParameters() {
        ContextParameters.get().clear();
    }

    public static Map<String, String> getContextParameters() {
        return ContextParameters.get();
    }



}
