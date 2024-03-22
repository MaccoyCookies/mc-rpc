package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author Maccoy
 * @date 2024/3/17 20:02
 * Description
 */
public interface RegisterCenter {

    void start();

    void stop();

    void register(String service, InstanceMeta instance);

    void unregister(String service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(String serviceName);

     void subscribe(String service, ChangedListener listener);

    class StaticRegisterCenter implements RegisterCenter {

        List<InstanceMeta> providers;

        public StaticRegisterCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String serviceName) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }



}
