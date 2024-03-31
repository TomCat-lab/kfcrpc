package com.cola.kfcrpc.core.api;

import java.util.List;

/**
 * Class: LoadBalancer
 * Author: cola
 * Date: 2024/3/31
 * Description: 负载均衡
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);

    LoadBalancer Default = p -> (p == null || p.size() ==0) ?null: p.get(0);
}
