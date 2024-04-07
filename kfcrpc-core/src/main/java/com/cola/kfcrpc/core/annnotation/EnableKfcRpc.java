package com.cola.kfcrpc.core.annnotation;

import com.cola.kfcrpc.core.consumer.ConsumserConfig;
import com.cola.kfcrpc.core.provider.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumserConfig.class})
public @interface EnableKfcRpc {
}
