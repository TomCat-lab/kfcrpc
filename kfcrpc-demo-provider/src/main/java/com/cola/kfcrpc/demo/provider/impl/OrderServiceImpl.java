package com.cola.kfcrpc.demo.provider.impl;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.demo.api.Order;
import com.cola.kfcrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * Class: OrderServiceImpl
 * Author: cola
 * Date: 2024/3/30
 * Description:
 */


@Component
@KfcProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {

        if(id == 404) {
            throw new RuntimeException("404 exception");
        }

        return new Order(id.longValue(), 15.6f);
    }
}
