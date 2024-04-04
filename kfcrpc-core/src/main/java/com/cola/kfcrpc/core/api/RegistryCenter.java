package com.cola.kfcrpc.core.api;

import com.cola.kfcrpc.core.meta.InstanceMeta;
import com.cola.kfcrpc.core.registry.ChagedListener;

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
    void  register(String service,InstanceMeta instance);

    void  unRegister(String service,InstanceMeta instance);

    List<InstanceMeta> fetchAll(String service);

     void subscribe(String service,ChagedListener chagedListener);

    class staticRegistryCenter implements RegistryCenter{

        private List<InstanceMeta> providers;

        public staticRegistryCenter(List<InstanceMeta> providers) {
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
        public void unRegister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChagedListener chagedListener) {

        }
    }
}
