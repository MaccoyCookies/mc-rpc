package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/17 18:06
 * Description
 */
public interface Router {

    List<InstanceMeta> router(List<InstanceMeta> providers);

    Router Default = providers -> providers;

}
