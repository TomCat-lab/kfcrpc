package com.cola.kfcrpc.demo.provider.impl;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
@KfcProvider
public class UserServiceImpl implements UserService {
    public User findById(Integer id) {
        return new User("kfc",id);
    }

    @Override
    public String getName() {
        return "cola and kfc";
    }

    @Override
    public String getName(int id) {
        return String.valueOf(id);
    }

    @Override
    public User findById(int id, String name) {
         return new User(name,id);
    }

    @Override
    public long getId(long i) {
        return 1000;
    }

    @Override
    public long getId(float i) {
        return 1L;
    }

    @Override
    public long getId(User user) {
        return user.getId();
    }

    @Override
    public long[] getIds(int[] ints) {
        return new long[]{1,2,3};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1,2,3};
    }

    @Override
    public List<LinkedHashMap<String, String>> getIds(List<User> users) {
        List<LinkedHashMap<String,String>> map = new ArrayList<>();
        LinkedHashMap<String,String> res = new LinkedHashMap<>();
        User user = users.get(0);
        res.put("cola",user.getName());
        map.add(res);
        return map;
    }
}
