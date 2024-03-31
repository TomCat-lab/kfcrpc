package com.cola.kfcrpc.core.cluster;

import com.cola.kfcrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class: RoundRibbonLoadBalancer
 * Author: cola
 * Date: 2024/3/31
 * Description: 轮询算法
 */

public class RoundRibbonLoadBalancer implements LoadBalancer {
    private final AtomicInteger index = new AtomicInteger(0);
    @Override
    public String choose(List<String> p) {
        if (p == null || p.size() ==0)  return null;
        if(p.size() == 1) return p.get(0);
        return p.get((index.getAndDecrement() & 0x7ffffff) % p.size());
    }
}
