package com.maccoy.mcrpc.core.api;

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

    void register(String service, String instance);

    void unregister(String service, String instance);

    List<String> fetchAll(String serviceName);

     void subscribe(String service, ChangedListener listener);

    class StaticRegisterCenter implements RegisterCenter {

        List<String> providers;

        public StaticRegisterCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String serviceName) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }



}
