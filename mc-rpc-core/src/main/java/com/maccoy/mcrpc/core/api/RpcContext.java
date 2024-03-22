package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/17 19:50
 * Description
 */
@Data
public class RpcContext {

    List<Filter> filters;

    Router router;

    LoadBalancer loadBalancer;

}
