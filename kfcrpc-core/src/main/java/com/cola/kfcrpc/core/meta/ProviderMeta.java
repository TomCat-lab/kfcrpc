package com.cola.kfcrpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * Class: ProviderMeta
 * Author: cola
 * Date: 2024/3/30
 * Description:
 */
@Data
@Builder
public class ProviderMeta {
    private Object impl;
    private String methodSign;
    private Method method;
}
