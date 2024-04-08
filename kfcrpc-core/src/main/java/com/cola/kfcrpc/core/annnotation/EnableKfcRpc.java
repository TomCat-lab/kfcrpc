package com.cola.kfcrpc.core.annnotation;

import com.cola.kfcrpc.core.config.ConsumserConfig;
import com.cola.kfcrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumserConfig.class})
public @interface EnableKfcRpc {
}
