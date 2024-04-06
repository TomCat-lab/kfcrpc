package com.cola.kfcrpc.demo.provider.impl;

import com.cola.kfcrpc.core.annnotation.KfcProvider;
import com.cola.kfcrpc.demo.api.User;
import com.cola.kfcrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@KfcProvider
public class UserServiceImpl implements UserService {
    @Autowired
    Environment environment;
    public User findById(Integer id) {
        String port = environment.getProperty("server.port");
        long l = System.currentTimeMillis();
        return new User("kfc_"+port+"_"+l,id);
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
    public Map<String, User>  getMap(Map<String, User> map) {
        return map;
    }

    @Override
    public List<User> getList(List<User> users) {
        return users;
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

    @Override
    public Boolean getFlag(boolean flag) {
        return !flag;
    }

    @Override
    public User findById(long id) {
        return new User("cola",Long.valueOf(id).intValue());
    }

    @Override
    public User ex(boolean flag) {
        if(flag) throw new RuntimeException("just throw an exception");
        return new User("KFC100",100);
    }

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if ("8083".equals(port)){
            try {
                Thread.sleep(Long.parseLong(port));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new User("no time out"+port,200);
    }

    @Override
    public User[] findUsers(User[] users) {
        return users;
    }
}
