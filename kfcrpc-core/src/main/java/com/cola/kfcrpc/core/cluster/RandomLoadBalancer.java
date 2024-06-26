package com.cola.kfcrpc.core.cluster;

import com.cola.kfcrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
/**
 * Class: RandomLoadBalancer
 * Author: cola
 * Date: 2024/3/31
 * Description: 随机轮询算法
 */

public class RandomLoadBalancer<T> implements LoadBalancer<T> {
    Random r = new Random();
    @Override
    public T choose(List<T> p) {
        if (p == null || p.size() ==0)  return null;
        if(p.size() == 1) return p.get(0);
        return p.get(r.nextInt(p.size()));
    }
}
