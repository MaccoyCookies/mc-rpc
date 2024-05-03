package com.maccoy.mcrpc.core.api;

import com.maccoy.mcrpc.core.meta.InstanceMeta;
import com.maccoy.mcrpc.core.meta.ServiceMeta;
import com.maccoy.mcrpc.core.registry.ChangedListener;

import java.util.List;
import java.util.Set;

/**
 * @author Maccoy
 * @date 2024/3/17 20:02
 * Description
 */
public interface RegisterCenter {

    void start();

    void stop();

    void register(ServiceMeta service, InstanceMeta instance);

    void unregister(ServiceMeta service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(ServiceMeta serviceMeta);

     void subscribe(ServiceMeta service, ChangedListener listener);

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
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta serviceMeta) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }

    }



}
