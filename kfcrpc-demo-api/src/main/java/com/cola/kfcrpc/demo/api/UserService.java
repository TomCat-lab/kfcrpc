package com.cola.kfcrpc.demo.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface UserService {
   User findById(Integer id);

   User findById(int id, String name);

    String getName();
    String getName(int id);

    long getId(long i);
    long getId(float i);

    long getId(User user);

    long[] getLongIds();

    long[] getIds(int[] ints);

    List<LinkedHashMap<String, String>> getIds(List<User> ints);

    List<User> getList(List<User> users);

  Map<String, User>    getMap(Map<String, User> map);


    Boolean getFlag(boolean flag);

    User findById(long id);

    User ex(boolean flag);

    User find(int timeout);

    User[] findUsers(User[] users);
}
