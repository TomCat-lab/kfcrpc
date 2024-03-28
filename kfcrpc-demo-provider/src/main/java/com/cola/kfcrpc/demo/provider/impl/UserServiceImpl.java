package com.cola.kfcrpc.demo.provider.impl;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import org.springframework.stereotype.Service;


@Service
@KfcProvider
public class UserServiceImpl implements UserService {
    public User findById(Integer id) {
        return new User("kfc",id);
    }
}
