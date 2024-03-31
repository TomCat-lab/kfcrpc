package com.cola.kfcrpc.core.api;

import java.util.List;

/**
 * Class: RegistryCenter
 * Author: cola
 * Date: 2024/3/31
 * Description: 注册中心
 */

public interface RegistryCenter {
    void start();

    void stop();

    // provider
    void  register(String service,String instance);

    void  unRegister(String service,String instance);

    List<String> fetchAll(String service);

    // void subscribe();

    class staticRegistryCenter implements RegistryCenter{

        private List<String> providers;

        public staticRegistryCenter(List<String> providers) {
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
        public void unRegister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }
    }
}
