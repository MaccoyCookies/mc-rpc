package com.maccoy.mcrpc.core.api;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/17 18:06
 * Description
 */
public interface Router<T> {

    List<T> router(List<T> providers);

    Router Default = providers -> providers;

}
