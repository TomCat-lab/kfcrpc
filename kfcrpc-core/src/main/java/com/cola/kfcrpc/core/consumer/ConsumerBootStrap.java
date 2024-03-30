package com.cola.kfcrpc.core.consumer;

import com.cola.kfcrpc.core.annnotation.KfcConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class: ConsumerBootStrap
 * Author: cola
 * Date: 2024/3/28
 * Description: 消费者启动类
 */
@Data
public class ConsumerBootStrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    Map<String, Object> skeleton = new HashMap<>();
    
    public void start(){
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            //todo filter package name
            List<Field> fields = new ArrayList<>();
            Object bean = applicationContext.getBean(beanDefinitionName);
            Arrays.stream(bean.getClass().getSuperclass().getDeclaredFields())
                    .forEach(f->{
                       if (f.isAnnotationPresent(KfcConsumer.class)){
                           fields.add(f);
                       }
                    });
            if (!CollectionUtils.isEmpty(fields)){
                fields.stream().forEach(
                        f->{
                            Object serviceImpl = null;
                            Class<?> anInterface = f.getType();
                            String service = anInterface.getCanonicalName();
                            if (skeleton.get(service) == null){
                                serviceImpl = createSkeleton(anInterface);
                                skeleton.put(service,serviceImpl);
                            }
                            f.setAccessible(true);
                            try {
                                f.set(bean,serviceImpl);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }finally {
                                f.setAccessible(false);
                            }

                        }
                );
            }
        }
    }

    private Object createSkeleton(Class<?> service) {
        Object proxyImpl = Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new KfcInvocationHandler(service));
        return proxyImpl;
    }
}
